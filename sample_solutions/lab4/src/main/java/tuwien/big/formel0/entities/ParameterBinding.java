package tuwien.big.formel0.entities;

class ParameterBinding {

    private String parameterName;
    private Object parameterValue;

    ParameterBinding(String parameterName, Object parameterValue) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Object getParameterValue() {
        return parameterValue;
    }
}