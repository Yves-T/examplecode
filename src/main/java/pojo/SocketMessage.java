package pojo;

import java.text.DecimalFormat;

public class SocketMessage {
    private String temperature;

    public String getTemperature() {
        return convertTemperatureFromFahrenheitToCelsius();
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    private String convertTemperatureFromFahrenheitToCelsius() {
        Float temperatureAsInteger = Float.parseFloat(temperature);
        Float temperatureCelsius = ((temperatureAsInteger - 32) * 5.0F / 9);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(temperatureCelsius);
    }
}
