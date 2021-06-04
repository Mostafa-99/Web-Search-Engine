package Backend.jpa.controller;

import Backend.jpa.services.LinkService;
import Backend.jpa.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Length")
public class LinkEndPoint {
    @Autowired
    private SearchService searchservice;
    @Autowired
    private LinkService linkservice;

//    @RequestMapping(method = RequestMethod.GET, value = "/{WordName}")
//    public ResponseEntity<Long> getNumlinks(@PathVariable String WordName) {
//
//        return linkservice.GetLinksCount(WordName);
//    }

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(method = RequestMethod.GET, value = "/{WordName}")
public ResponseEntity<?> getNumlinks(@PathVariable String WordName) {

    return linkservice.GetLinksCount(WordName);
}
}
