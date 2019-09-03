/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package be.nbb.sdmx.facade.file;

import be.nbb.sdmx.facade.DataStructureRef;
import be.nbb.sdmx.facade.Dataflow;
import be.nbb.sdmx.facade.LanguagePriorityList;
import be.nbb.sdmx.facade.parser.DataFactory;
import be.nbb.sdmx.facade.parser.spi.SdmxDialect;
import internal.file.CachedResource;
import internal.file.SdmxDecoder;
import internal.file.SdmxFileConnectionImpl;
import internal.file.SdmxFileUtil;
import internal.file.xml.StaxSdmxDecoder;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import be.nbb.sdmx.facade.SdmxManager;
import be.nbb.sdmx.facade.parser.spi.SdmxDialectLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
@lombok.Value
@lombok.Builder(builderClassName = "Builder", toBuilder = true)
@lombok.experimental.Wither
public final class SdmxFileManager implements SdmxManager {

    @NonNull
    public static SdmxFileManager ofServiceLoader() {
        return builder().dialects(new SdmxDialectLoader().get()).build();
    }

    private static final DataStructureRef EMPTY = DataStructureRef.of("", "", "");

    @lombok.NonNull
    private final LanguagePriorityList languages;

    @lombok.NonNull
    private final SdmxDecoder decoder;

    @lombok.NonNull
    private final ConcurrentMap cache;

    @lombok.NonNull
    @lombok.Singular
    private final List<SdmxDialect> dialects;

    // Fix lombok.Builder.Default bug in NetBeans
    public static Builder builder() {
        return new Builder()
                .languages(LanguagePriorityList.ANY)
                .decoder(new StaxSdmxDecoder())
                .cache(new ConcurrentHashMap<>());
    }

    @Override
    public SdmxFileConnection getConnection(String name) throws IOException {
        return getConnection(getFiles(name));
    }

    @NonNull
    public SdmxFileConnection getConnection(@NonNull SdmxFileSet files) throws IOException {
        return new SdmxFileConnectionImpl(getResource(files), getDataflow(files));
    }

    private SdmxFileSet getFiles(String name) throws IOException {
        try {
            return SdmxFileUtil.fromXml(name);
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex.getMessage(), ex.getCause());
        }
    }

    private SdmxFileConnectionImpl.Resource getResource(SdmxFileSet files) {
        return new CachedResource(files, languages, decoder, getDataFactory(files), cache);
    }

    private Dataflow getDataflow(SdmxFileSet files) {
        return Dataflow.of(files.asDataflowRef(), EMPTY, SdmxFileUtil.asFlowLabel(files));
    }

    private Optional<DataFactory> getDataFactory(SdmxFileSet files) {
        return dialects.stream()
                .filter(o -> o.getName().equals(files.getDialect()))
                .map(DataFactory.class::cast)
                .findFirst();
    }
}
