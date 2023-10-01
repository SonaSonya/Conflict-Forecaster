package com.conflict.forecaster.service;

import com.conflict.forecaster.database.entity.UCDPEvent;
import com.conflict.forecaster.database.UCDPEventRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class UCDPApiClient {

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

    String[] versions2023 = {"23.0.1", "23.0.2", "23.0.3", "23.0.4", "23.0.5", "23.0.6", "23.0.7"};
    String[] versions2018_2022 = {"18.0.1", "18.0.2", "18.0.3", "18.0.4", "18.0.5", "18.0.6", "18.0.7", "18.0.8", "18.0.9", "18.0.10", "18.0.11", "18.0.12",
            "19.0.1", "19.0.2", "19.0.3", "19.0.4", "19.0.5", "19.0.6", "19.0.7", "19.0.8", "19.0.9", "19.0.10", "19.0.11", "19.0.12",
            "20.0.1", "20.0.2", "20.0.3", "20.0.4", "20.0.5", "20.0.6", "20.0.7", "20.0.8", "20.0.9", "20.0.10", "20.0.11", "20.0.12",
            "21.0.1", "21.0.2", "21.0.3", "21.0.4", "21.0.5", "21.0.6", "21.0.7", "21.0.8", "21.0.9", "21.0.10", "21.0.11", "21.0.12",
            "22.0.1", "22.0.2", "22.0.3", "22.0.4", "22.0.5", "22.0.6", "22.0.7", "22.0.8", "22.0.9", "22.0.10", "22.0.11", "22.0.12",
    };

    @Autowired
    private UCDPEventRepository ucdpEventRepository;

    public UCDPApiClient() {
    }

    // Сохранение событий из UCDP за 2018-2022 годы для всех африканских стран (для заполнения таблицы, использовалась 1 раз)
    public void saveEvents2018_2022 () throws IOException, ParseException {
        var i = 0;
        while (i < africanCountries.length) {
            saveEvents(africanCountries[i], versions2018_2022);
            i++;
        }
    }

    // Сохранение событий из UCDP за 2023 год для всех африканских стран
    public void saveEvents2023 () throws IOException, ParseException {
        var i = 0;
        while (i < africanCountries.length) {
            saveEvents(africanCountries[i], versions2023);
            i++;
        }
    }

    // Запрос событий из UCDP для страны и версий (месяцев), сохранение результатов в базу
    public void saveEvents (int country, String[] versions) throws IOException, ParseException {

        var i = 0;
        while (i < versions.length) {
            // Для каждой версии (месяца)

            String url = getUrl(country, versions[i], 1000, 0);

            while (!url.equals("")) {
                // Для каждой страницы
                JSONObject events = requestEvents(url);
                saveEventResponse(events);

                url = getNextPageUrl(events);
            }

            i++;
        }
    }

    // Функция генерации url для доступа к api ucdp
    public String getUrl (int country, String version, int pageSize, int page) {
        return "https://ucdpapi.pcr.uu.se/api/gedevents/" + version + "?pagesize=" + pageSize + "&page=" + page + "&country=" + country;
    }

    // Запрос JSON событий из api ucdp
    public JSONObject requestEvents (String url) throws IOException, ParseException {
        // TODO: Заменить на RestTemplate
        URL objURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) objURL.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseJSONEvent(response.toString());
    }

    // Считывание json из строки
    public JSONObject parseJSONEvent (String event) throws ParseException {
        // Считываем json
        Object obj = new JSONParser().parse(event);

        // Кастим obj в JSONObject
        JSONObject jo = (JSONObject) obj;

        return jo;
    }

    // Запрос следующей страницы (в случае, если события разделены на несколько страниц)
    public String getNextPageUrl (JSONObject response) {
        return (String) response.get("NextPageUrl");
    }

    // Запись событий в базу из ответа ucdp
    public void saveEventResponse (JSONObject response) {

        // Достаем массив событий
        JSONArray result = (JSONArray) response.get("Result");
        Iterator resultItr = result.iterator();


        Long id_ucdp;

        int year, month, type_of_violence;
        String conflict_name, dyad_name, side_a, side_b, adm_1, adm_2, country, region, date_start, date_end;
        Long conflict_id, dyad_id, side_a_id, side_b_id, priogrid_gid, country_id, date_prec;
        Long deaths_a, deaths_b, deaths_civilians, deaths_unknown, deaths_all;
        double latitude, longitude;

            // Выводим в цикле данные массива
        while (resultItr.hasNext()) {
            JSONObject event = (JSONObject) resultItr.next();

            LocalDateTime dateTime = LocalDateTime.parse(event.get("date_start").toString());

            id_ucdp             = (Long) event.get("id");
            year                = ((Long) event.get("year")).intValue();
            month               = dateTime.getMonthValue();
            type_of_violence    = ((Long) event.get("type_of_violence")).intValue();
            conflict_name       = (event.get("conflict_name") == null) ? "" : event.get("conflict_name").toString();
            dyad_name           = (event.get("dyad_name") == null) ? "" : event.get("dyad_name").toString();
            side_a              = (event.get("side_a") == null) ? "" : event.get("side_a").toString();
            side_b              = (event.get("side_b") == null) ? "" : event.get("side_b").toString();
            adm_1               = (event.get("adm_1") == null) ? "" : event.get("adm_1").toString();
            adm_2               = (event.get("adm_2") == null) ? "" : event.get("adm_2").toString();
            country             = (event.get("country") == null) ? "" : event.get("country").toString();
            region              = (event.get("region") == null) ? "" : event.get("region").toString();
            date_start          = (event.get("date_start") == null) ? "" : event.get("date_start").toString();
            date_end            = (event.get("date_end") == null) ? "" : event.get("date_end").toString();
            conflict_id         = (Long) event.get("conflict_new_id");
            dyad_id             = (Long) event.get("dyad_new_id");
            side_a_id           = (Long) event.get("side_a_new_id");
            side_b_id           = (Long) event.get("side_b_new_id");
            priogrid_gid        = (Long) event.get("priogrid_gid");
            country_id          = (Long) event.get("country_id");
            date_prec           = (Long) event.get("date_prec");
            deaths_a            = (Long) event.get("deaths_a");
            deaths_b            = (Long) event.get("deaths_b");
            deaths_civilians    = (Long) event.get("deaths_civilians");
            deaths_unknown      = (Long) event.get("deaths_unknown");
            deaths_all          = (Long) event.get("best");
            latitude            = (event.get("latitude") == null) ? 0 : (double) event.get("latitude");
            longitude           = (event.get("longitude") == null) ? 0 : (double) event.get("longitude");

            UCDPEvent ucdpEvent = new UCDPEvent(id_ucdp, year, month, type_of_violence, conflict_name, dyad_name, side_a, side_b, adm_1, adm_2, country, region, date_start, date_end, conflict_id, dyad_id, side_a_id, side_b_id, priogrid_gid, country_id, date_prec, deaths_a, deaths_b, deaths_civilians, deaths_unknown, deaths_all, latitude, longitude);
            ucdpEventRepository.save(ucdpEvent);
        }
    }

    // Вывод ответа ucdp в консоль
    public void printEventResponse (JSONObject response) {
        // Достаём TotalCount and TotalPages
        System.out.println("TotalCount: " + response.get("TotalCount"));
        System.out.println("TotalPages: " + response.get("TotalPages"));
        // Достаем массив номеров
        JSONArray result = (JSONArray) response.get("Result");
        Iterator resultItr = result.iterator();
        System.out.println("event data: ");
        // Выводим в цикле данные массива
        while (resultItr.hasNext()) {
            JSONObject event = (JSONObject) resultItr.next();
            System.out.println("- " + event.get("id"));
            System.out.println("- " + event.get("year"));
            System.out.println("- " + event.get("type_of_violence"));
            System.out.println("- " + event.get("conflict_name"));
            System.out.println("- " + event.get("conflict_new_id"));
            System.out.println("- " + event.get("dyad_name"));
            System.out.println("- " + event.get("dyad_new_id"));
            System.out.println("- " + event.get("side_a"));
            System.out.println("- " + event.get("side_a_new_id"));
            System.out.println("- " + event.get("side_b"));
            System.out.println("- " + event.get("side_b_new_id"));
            System.out.println("- " + event.get("adm_1"));
            System.out.println("- " + event.get("adm_2"));
            System.out.println("- " + event.get("latitude"));
            System.out.println("- " + event.get("longitude"));
            System.out.println("- " + event.get("priogrid_gid"));
            System.out.println("- " + event.get("country"));
            System.out.println("- " + event.get("country_id"));
            System.out.println("- " + event.get("region"));
            System.out.println("- " + event.get("date_prec"));
            System.out.println("- " + event.get("date_start"));
            System.out.println("- " + event.get("date_end"));
            System.out.println("- " + event.get("deaths_a"));
            System.out.println("- " + event.get("deaths_b"));
            System.out.println("- " + event.get("deaths_civilians"));
            System.out.println("- " + event.get("deaths_unknown"));
            System.out.println("- " + event.get("best"));

        }

    }
}
