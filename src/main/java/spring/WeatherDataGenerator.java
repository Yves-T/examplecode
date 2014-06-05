package spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pojo.SocketMessage;
import pojo.weather.Channel;
import pojo.weather.Condition;
import pojo.weather.Item;
import pojo.weather.Query;
import pojo.weather.Result;
import pojo.weather.Results;

import java.io.IOException;
import java.net.URL;

@Component
public class WeatherDataGenerator implements
        ApplicationListener<BrokerAvailabilityEvent> {

    private static final Logger log = LogManager.getLogger(WeatherDataGenerator.class);

    private final MessageSendingOperations<String> messagingTemplate;

    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public WeatherDataGenerator(
            final MessageSendingOperations<String> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onApplicationEvent(final BrokerAvailabilityEvent event) {
    }

    // fetch pojo.weather json ( delay is in milliseconds )
    @Scheduled(fixedDelay = 10000)
    public void sendDataUpdates() throws JsonProcessingException {
        WeatherResults weatherResults = new WeatherResults(objectMapper);
        Result result = weatherResults.getWeatherResults();

        SocketMessage socketMessage = createSocketMessage(result);

        this.messagingTemplate.convertAndSend(
                "/topic/data", socketMessage);
    }

    private SocketMessage createSocketMessage(Result result) throws JsonProcessingException {
        Query query = result.getQuery();
        Results results = query.getResults();
        Channel channel = results.getChannel();
        Item item = channel.getItem();
        Condition condition = item.getCondition();

        String temperature = condition.getTemp();

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setTemperature(temperature);

        return socketMessage;
    }

    static private class WeatherResults {
        public static final String WEATHER_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20item%20from" +
                "%20weather.forecast%20where%20location%3D%22BEXX0003%22&format=json";
        private ObjectMapper mapper;

        public WeatherResults(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        public Result getWeatherResults() {
            Result query = null;
            try {
                URL url = new URL(WEATHER_URL);
                query = mapper.readValue(url, Result.class);
            } catch (IOException e) {
                log.error("Error when fetching pojo.weather");
            }
            return query;
        }
    }
}