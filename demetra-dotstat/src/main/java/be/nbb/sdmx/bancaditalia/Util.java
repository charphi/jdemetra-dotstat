/*
 * Copyright 2015 National Bank of Belgium
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
package be.nbb.sdmx.bancaditalia;

import be.nbb.sdmx.DataStructure;
import be.nbb.sdmx.ResourceRef;
import be.nbb.sdmx.FlowRef;
import it.bancaditalia.oss.sdmx.api.Codelist;
import it.bancaditalia.oss.sdmx.api.DSDIdentifier;
import it.bancaditalia.oss.sdmx.api.DataFlowStructure;
import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.api.Dimension;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass
final class Util {

    public static be.nbb.sdmx.Dataflow toDataflow(Dataflow dataflow) {
        return new be.nbb.sdmx.Dataflow(FlowRef.parse(dataflow.getFullIdentifier()), toDataStructureRef(dataflow.getDsdIdentifier()), dataflow.getDescription());
    }

    public static be.nbb.sdmx.ResourceRef toDataStructureRef(DSDIdentifier input) {
        return new ResourceRef(input.getAgency(), input.getId(), input.getVersion());
    }

    static be.nbb.sdmx.Codelist toCodelist(Codelist input) {
        return new be.nbb.sdmx.Codelist(new ResourceRef(input.getAgency(), input.getId(), input.getVersion()), input.getCodes());
    }

    public static DataStructure toDataStructure(DataFlowStructure dfs) {
        Set<be.nbb.sdmx.Dimension> dimensions = new HashSet<>();
        for (Dimension o : dfs.getDimensions()) {
            dimensions.add(new be.nbb.sdmx.Dimension(o.getId(), o.getPosition(), toCodelist(o.getCodeList()), o.getName()));
        }
        return new DataStructure(new ResourceRef(dfs.getAgency(), dfs.getId(), dfs.getVersion()), dimensions, dfs.getName(), dfs.getTimeDimension(), dfs.getMeasure());
    }
}
