package com.conflict.forecaster.service;

import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEvent;
import com.conflict.forecaster.database.UCDPEventRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UCDPApiClientService {

    int[] africanCountries  = {
            615, // Algeria
            540, // Angola
            434, // Benin
            571, // Botswana
            439, // Burkina Faso
            516, // Burundi
            471, // Cameroon
            402, // Cape Verde
            482, // Central African Republic
            483, // Chad
            581, // Comoros
            490, // DR Congo (Zaire)
            484, // Congo
            522, // Djibouti
            651, // Egypt
            411, // Equatorial Guinea
            531, // Eritrea
            530, // Ethiopia
            481, // Gabon
            420, // Gambia
            452, // Ghana
            438, // Guinea
            404, // Guinea-Bissau
            501, // Kenya
            570, // Lesotho
            450, // Liberia
            620, // Libya
            580, // Madagascar
            553, // Malawi
            432, // Mali
            435, // Mauritania
            590, // Mauritius
            600, // Morocco
            541, // Mozambique
            565, // Namibia
            436, // Niger
            475, // Nigeria
            517, // Rwanda
            433, // Senegal
            451, // Sierra Leone
            520, // Somalia
            560, // South Africa
            626, // South Sudan
            625, // Sudan
            510, // Tanzania
            461, // Togo
            616, // Tunisia
            500, // Uganda
            551, // Zambia
            552, // Zimbabwe
    };

    String[] versions = {"18.0.1", "18.0.2", "18.0.3", "18.0.4", "18.0.5", "18.0.6", "18.0.7", "18.0.8", "18.0.9", "18.0.10", "18.0.11", "18.0.12",
            "19.0.1", "19.0.2", "19.0.3", "19.0.4", "19.0.5", "19.0.6", "19.0.7", "19.0.8", "19.0.9", "19.0.10", "19.0.11", "19.0.12",
            "20.0.1", "20.0.2", "20.0.3", "20.0.4", "20.0.5", "20.0.6", "20.0.7", "20.0.8", "20.0.9", "20.0.10", "20.0.11", "20.0.12",
            "21.0.1", "21.0.2", "21.0.3", "21.0.4", "21.0.5", "21.0.6", "21.0.7", "21.0.8", "21.0.9", "21.0.10", "21.0.11", "21.0.12",
            "22.0.1", "22.0.2", "22.0.3", "22.0.4", "22.0.5", "22.0.6", "22.0.7", "22.0.8", "22.0.9", "22.0.10", "22.0.11", "22.0.12",
            "23.0.1", "23.0.2", "23.0.3", "23.0.4", "23.0.5", "23.0.6", "23.0.7", "23.0.8", "23.0.9", "23.0.10", "23.0.11", "23.0.12",
            "24.0.1", "24.0.2"
    };

    private UCDPEventRepository ucdpEventRepository;
    private UCDPEventCountRepository ucdpEventCountRepository;

    public UCDPApiClientService(UCDPEventRepository ucdpEventRepository, UCDPEventCountRepository ucdpEventCountRepository) {
        this.ucdpEventRepository = ucdpEventRepository;
        this.ucdpEventCountRepository = ucdpEventCountRepository;
    }

    public boolean hasData() {
        List<UCDPEvent> data = (List<UCDPEvent>) ucdpEventRepository.findAll();
        return !data.isEmpty();
    }

    public void clearData() {
        ucdpEventRepository.deleteAll();
        ucdpEventCountRepository.deleteAll();
    }

    public long initialize (String startDate, String endDate) throws IOException, ParseException {
        if (hasData()) {
            System.out.println("clearing");
            clearData();
        }

        long rowsSaved = fillDatabase(startDate, endDate);

        initializeUCDPEventCount();
        return rowsSaved;
    }

    public Long update (String endDate) throws IOException, ParseException {
        UCDPEvent latestEvent = ucdpEventRepository.findFirst1ByOrderByYearDescMonthDesc().get(0);
        String latestVersion = (latestEvent.getYear() % 100) + ".0." + latestEvent.getMonth()  ;
        System.out.println("latest " + latestVersion);
        System.out.println("endDate " + endDate);
        long rowsSaved = fillDatabase(latestVersion, endDate);
        System.out.println("rowsSaved " + rowsSaved);
        initializeUCDPEventCount();
        return rowsSaved;
    }

    public long fillDatabase (String startDate, String endDate) throws IOException, ParseException {

        String[] versionsArray = getVersions(startDate, endDate);
        System.out.println("versions:");
        System.out.println(Arrays.toString(versionsArray));
        long rowsSaved = 0;
        var i = 0;
        while (i < africanCountries.length) {
            System.out.println("country " + africanCountries[i]);
            rowsSaved += saveEvents(africanCountries[i], versionsArray);
            i++;
        }
        return rowsSaved;
    }

    private void initializeUCDPEventCount() {
        ucdpEventCountRepository.deleteAll();
        List<UCDPEventCount> counts = ucdpEventRepository.findCounts();
        //Long i = Long.valueOf(1);
        for (UCDPEventCount c : counts ) {
            //c.setId(i);
            ucdpEventCountRepository.save(c);
            //i++;
        }
        System.out.println("here");
    }

    public String[] getVersions(String startDate, String endDate) {
        int index = 0;
        boolean insideRange = false;
        ArrayList<String> rangeList = new ArrayList<>();

        while (index < versions.length) {
            String version = versions[index];

            if (version.equals(startDate)) {
                insideRange = true;
            }

            if (insideRange) {
                rangeList.add(version); // Добавляем версии в ArrayList
            }

            if (version.equals(endDate)) {
                insideRange = false;
                break;
            }

            index++;
        }

        // Преобразуем ArrayList в массив
        String[] rangeArray = rangeList.toArray(new String[rangeList.size()]);
        return rangeArray;
    }

    // Запрос событий из UCDP для страны и версий (месяцев), сохранение результатов в базу
    public long saveEvents (int country, String[] versions) throws IOException, ParseException {

        long rowsSaved = 0;
        var i = 0;
        while (i < versions.length) {
            // Для каждой версии (месяца)
            System.out.println(versions[i]);
            String url = getUrl(country, versions[i], 1000, 0);

            while (!url.isEmpty()) {
                String json = requestEvents(url);
                List<UCDPEvent> events = extractObjectsFromJson(json);
                rowsSaved += saveEventResponse(events);

                url = getNextPageUrl(json);
            }

            i++;
        }

        return rowsSaved;
    }

    // Функция генерации url для доступа к api ucdp
    public String getUrl (int country, String version, int pageSize, int page) {
        return "https://ucdpapi.pcr.uu.se/api/gedevents/" + version + "?pagesize=" + pageSize + "&page=" + page + "&country=" + country;
    }

    // Запрос JSON событий из api ucdp
    public String requestEvents (String url) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, String.class);
    }

    public List<UCDPEvent> extractObjectsFromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode rootNode = objectMapper.readTree(json);

        JsonNode dataNode = rootNode.get("Result");
        if (dataNode != null && dataNode.isArray()) {
            List<UCDPEvent> events = new ArrayList<>();

            for (JsonNode item : dataNode) {
                UCDPEvent event = objectMapper.treeToValue(item, UCDPEvent.class);

                LocalDateTime dateTime = LocalDateTime.parse(event.getDate_start());
                event.setMonth(dateTime.getMonthValue());

                events.add(event);
            }

            return events;

        } else {
            return Collections.emptyList();
        }
    }

    // Запрос следующей страницы (в случае, если события разделены на несколько страниц)
    public String getNextPageUrl (String json) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        String url = rootNode.get("NextPageUrl").asText();

        return url;
    }

    // Запись событий в базу из ответа ucdp
    public long saveEventResponse (List<UCDPEvent> events) {
        long rowsSaved = 0;

        for (UCDPEvent event : events) {
            System.out.println("save row event: " + event.getMonth() + " " + event.getYear());
            ucdpEventRepository.save(event);
            rowsSaved++;
        }

        return rowsSaved;
    }
}
