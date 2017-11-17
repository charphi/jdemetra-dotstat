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

import java.io.File;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class SdmxFileSetTest {

    @Test
    @SuppressWarnings("null")
    public void testFactory() {
        assertThatThrownBy(() -> SdmxFileSet.of(null, null)).isInstanceOf(NullPointerException.class);

        assertThat(SdmxFileSet.of(data, null))
                .hasFieldOrPropertyWithValue("data", data)
                .hasFieldOrPropertyWithValue("structure", null);

        assertThat(SdmxFileSet.of(data, structure))
                .hasFieldOrPropertyWithValue("data", data)
                .hasFieldOrPropertyWithValue("structure", structure);
    }

    @Test
    public void testAsDataflowRef() {
        assertThat(SdmxFileSet.of(data, structure).asDataflowRef().toString())
                .isEqualTo("all,data&struct,latest");

        assertThat(SdmxFileSet.of(data, new File("")).asDataflowRef().toString())
                .isEqualTo("all,data,latest");

        assertThat(SdmxFileSet.of(data, null).asDataflowRef().toString())
                .isEqualTo("all,data,latest");
    }

    private final File data = new File("a.xml");
    private final File structure = new File("b.xml");
}
