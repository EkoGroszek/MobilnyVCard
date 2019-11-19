package com.example.demo.endpoints;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jsoup.Connection;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


@RestController
@RequestMapping("/api/search/")
public class VCardGenerator {


    @GetMapping(value = "byName/{name}")
    public ResponseEntity<String> getTest(@PathVariable("name") String name) throws IOException {
        Connection connect = Jsoup.connect("https://adm.edu.p.lodz.pl/user/users.php?search=" + name);
        Document document = connect.get();
        Elements elements = document.select("div.user-info");

//        Map<String, String> events = new HashMap();
        StringBuffer str = new StringBuffer();
        for (Element el : elements) {
            str.append(el.select("a").get(0).attr("title"));
        }

        String resource = str.toString();
        return new ResponseEntity<String>(resource, HttpStatus.OK);
    }
}
