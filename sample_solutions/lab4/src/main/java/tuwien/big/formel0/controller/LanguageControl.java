package tuwien.big.formel0.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "lang")
@RequestScoped
public class LanguageControl {

    private String language;

    /**
     * Creates a new instance of LanguageControl
     */
    public LanguageControl() {
    }

    /**
     * Returns the current language.
     *
     * @return the current language
     */
    public String getLanguage() {
        if (language == null) {
            language = "en".equals(FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().getLanguage()) ? "en" : "de";
        }

        return language;
    }
}
