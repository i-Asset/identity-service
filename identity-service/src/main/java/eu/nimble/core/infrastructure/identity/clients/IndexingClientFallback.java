package eu.nimble.core.infrastructure.identity.clients;

import at.srfg.indexing.model.party.PartyType;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dileepa Jayakody
 */
@Component
public class IndexingClientFallback implements IndexingClient {

    @Override
    public Boolean setParty(PartyType party,String bearerToken) {
        return false;
    }

    @Override
    public PartyType getParty(String uri,String bearerToken) {
        return null;
    }

    @Override
    public Boolean deleteParty(String uri,String bearerToken) {
        return false;
    }
//
//    @Override
//    public SearchResult searchItem( Search search,String bearerToken) {
//        return null;
//    }
//
//    @Override
//    public Boolean removeItem(String uri,String bearerToken) {
//        return false;
//    }
//
//    @Override
//    public Boolean deleteCatalogue(String uri,String bearerToken) {
//        return false;
//    }
}
