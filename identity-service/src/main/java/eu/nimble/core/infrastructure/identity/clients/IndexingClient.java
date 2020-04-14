package eu.nimble.core.infrastructure.identity.clients;

import at.srfg.indexing.model.party.PartyType;
//import eu.nimble.service.model.solr.Search;
//import eu.nimble.service.model.solr.SearchResult;
import feign.Headers;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST Client to index entities via the Indexing Service.
 *
 * @author Dileepa Jayakody
 */
@FeignClient(name = "indexing-service", url = "${nimble.indexing.url}", fallback = IndexingClientFallback.class)
public interface IndexingClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/party", consumes = "application/json")
    @Headers("Content-Type: application/json")
    Boolean setParty(@RequestBody PartyType party,
    @RequestHeader(value = "Authorization", required = true) String bearerToken);

    @RequestMapping(method = RequestMethod.GET, value = "/party")
    PartyType getParty(@RequestParam(value = "uri") String uri,
    @RequestHeader(value = "Authorization", required = true) String bearerToken);

    @RequestMapping(method = RequestMethod.DELETE, value = "/party")
    Boolean deleteParty(@RequestParam(value = "uri") String uri,
    @RequestHeader(value = "Authorization", required = true) String bearerToken);

//    @RequestMapping(method = RequestMethod.POST, value = "/item/search")
//    SearchResult searchItem(@RequestBody Search search,
//            @RequestHeader(value = "Authorization", required = true) String bearerToken);
//
//    @RequestMapping(method = RequestMethod.DELETE, value = "/item")
//    Boolean removeItem(@RequestParam(value = "uri") String uri,
//            @RequestHeader(value = "Authorization", required = true) String bearerToken);
//
//    @RequestMapping(method = RequestMethod.DELETE, value = "/catalogue")
//    Boolean deleteCatalogue(@RequestParam(value = "uri") String uri,
//            @RequestHeader(value = "Authorization", required = true) String bearerToken);
}
