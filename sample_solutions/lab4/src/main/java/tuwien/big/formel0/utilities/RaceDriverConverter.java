/*
 * Copyright 2013 petra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tuwien.big.formel0.utilities;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import tuwien.big.formel0.controller.RaceDriverControl;
import tuwien.big.formel0.picasa.RaceDriver;

/**
 *
 * @author petra
 */
@FacesConverter(forClass = RaceDriver.class)
public class RaceDriverConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) throws ConverterException {
        if (value == null) {
            return null;
        }

        RaceDriverControl rdc = fc.getApplication().evaluateExpressionGet(fc, "#{rdc}",
                RaceDriverControl.class);

        for (RaceDriver driver : rdc.getDrivers()) {
            if (driver.getName().equals(value)) {
                return driver;
            }
        }
        throw new ConverterException(new FacesMessage(String.format("Cannot convert %s to RaceDriver", value)));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object value) throws ConverterException {
        return (value instanceof RaceDriver) ? ((RaceDriver) value).getName() : null;
    }
}
