package com.example.demo.endpoints;


import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.jsoup.Connection;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/search/")
public class VCardGenerator {


    @GetMapping(value = "byName/{name}")
    public ResponseEntity<String> getTest(@PathVariable("name") String name) throws IOException {
        Connection connect = Jsoup.connect("https://adm.edu.p.lodz.pl/user/users.php?search=" + name);
        Document document = connect.get();
        Elements elements = document.select("div.user-info");
        StringBuffer buffer = new StringBuffer();
        buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");


        String button = "<form action= \"DOMEN" +
                " \"> " + "/api/html/users/" +
                "<input type =\"button\" value = \"Generate VCard\">" +
                " </form>";
        for (Element e : elements) {
            buffer.append(e.toString());
            buffer.append("<a href=" + "/api/search/generate/vcard/" + name + "/" + 1 + "><button>Generate Vcard</button></a>");
        }

        String resource = buffer.toString();
        return new ResponseEntity<String>(resource, HttpStatus.OK);
    }


    @GetMapping(value = "generate/vcard/{name}/{id}")
    public ResponseEntity<Resource> generateVCard(@PathVariable String name, @PathVariable int id) throws IOException {
        String url = "https://adm.edu.p.lodz.pl/user/users.php?search=" + name;
        StringBuffer buffer = new StringBuffer();
        Document doc = Jsoup.connect(url).get();
        Elements usersList = doc.select("div.user-info");

        Elements userName = usersList.select("h3");
        userName = userName.select("a");
        Elements affilationList = usersList.select("span.item-content");

        String finalName = "";
        String finalAffilation = "";

        int i = 0;
        for (Element e : userName) {
            i++;
            if (i == id) {
                finalName = e.text();
                break;
            }
        }
        i = 0;
        for (Element e : affilationList) {
            i++;
            if (i == id) {
                finalAffilation = e.text();
                break;
            }
        }

        VCard vcard = new VCard();
        vcard.setFormattedName(finalName);
        vcard.setOrganization(finalAffilation);
        File file = new File("card.vcf");
        Ezvcard.write(vcard).version(VCardVersion.V3_0).go(file);

        InputStreamResource resource = new InputStreamResource(new FileInputStream("card.vcf"));
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=" + finalName + ".vcf")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/vcf")).body(resource);

    }


    @GetMapping("/fromInput")
    public ResponseEntity<String> getFromInput(@RequestParam(name="name", required=true) String name) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://adm.edu.p.lodz.pl/user/users.php?search=" + name;
        StringBuffer buffer = new StringBuffer();
        buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");

        Document doc = Jsoup.connect(url).get();
        Elements users = doc.select("div.user-info");
        String button = "<form action= \"DOMEN" +
                " \"> " +"/api/html/users/" +
                "<input type =\"button\" value = \"Generate VCard\">" +
                " </form>";
        for (Element e : users) {
            buffer.append(e.toString());
            buffer.append("<a href=" + "/api/search/generate/vcard/" + name + "/" + 1 + "><button>Generate Vcard</button></a>");
        }

        return ResponseEntity.ok(buffer.toString());
    }
}
