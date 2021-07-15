package eu.nimble.core.infrastructure.identity.utils;

import at.srfg.iot.common.solr.model.model.party.PartyType;
import eu.nimble.service.model.ubl.commonaggregatecomponents.ContactType;
import eu.nimble.service.model.ubl.commonaggregatecomponents.DocumentReferenceType;

/**
 * Created by Dileepa Jayakody on 15/03/19
 * This util class gives helper methods to convert UBL data models to Solr Data Models for indexing.
 */
public class DataModelUtils {

    /**
     * UBL data model to Solr model converter
     */

    public static PartyType toIndexParty(eu.nimble.service.model.ubl.commonaggregatecomponents.PartyType party) {
        PartyType indexParty = new PartyType();
        party.getBrandName().forEach(name -> indexParty.addBrandName(name.getLanguageID(), name.getValue()));
        if(party.getPartyName() != null && party.getPartyName().size() > 0){
            indexParty.setLegalName(party.getPartyName().get(0).getName().getValue());
        }
        if(party.getPostalAddress() != null && party.getPostalAddress().getCountry() != null){
            String originLang = party.getPostalAddress().getCountry().getName().getLanguageID() != null ? party.getPostalAddress().getCountry().getName().getLanguageID() : "";
            indexParty.addOrigin(originLang, party.getPostalAddress().getCountry().getName().getValue());
        }
        // include the name, mail & phone of the main contact person (when set)
        if (party.getContact()!= null ) {
        	ContactType contact = party.getContact();
        	indexParty.setProperty(contact.getName().getValue(), "contact", "name");
        	indexParty.setProperty(contact.getElectronicMail(), "contact", "email");
        	indexParty.setProperty(contact.getTelephone(), "contact", "phone");
        }
        indexParty.setId(party.getHjid().toString());
        indexParty.setUri(party.getHjid().toString());

//        // TODO currently we do not support multilingual certificate types
//        party.getCertificate().stream().forEach(certificate -> indexParty.addCertificateType("", certificate.getCertificateTypeCode().getName()));
//        if(party.getPpapCompatibilityLevel() != null) {
//            indexParty.setPpapComplianceLevel(party.getPpapCompatibilityLevel().intValue());
//        }
//        // TODO currently we do not support multilingual ppap document types
//        party.getPpapDocumentReference().forEach(ppapDocument -> indexParty.addPpapDocumentType("", ppapDocument.getDocumentType()));
//
//        // get trust scores
//        party.getQualityIndicator().forEach(qualityIndicator -> {
//            if(qualityIndicator.getQualityParameter() != null && qualityIndicator.getQuantity() != null) {
//                if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.COMPANY_RATING.toString())) {
//                    indexParty.setTrustRating(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.TRUST_SCORE.toString())) {
//                    indexParty.setTrustScore(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.DELIVERY_PACKAGING.toString())) {
//                    indexParty.setTrustDeliveryPackaging(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.FULFILLMENT_OF_TERMS.toString())) {
//                    indexParty.setTrustFullfillmentOfTerms(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.SELLER_COMMUNICATION.toString())) {
//                    indexParty.setTrustSellerCommunication(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.NUMBER_OF_TRANSACTIONS.toString())) {
//                    indexParty.setTrustNumberOfTransactions(qualityIndicator.getQuantity().getValue().doubleValue());
//                } else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.TRADING_VOLUME.toString())) {
//                    indexParty.setTrustTradingVolume(qualityIndicator.getQuantity().getValue().doubleValue());
//                }
//            }
//        });
//        if (party.getIndustrySector() != null) {
//            List<TextType> industrySectors = party.getIndustrySector();
//            for (TextType sector : industrySectors) {
//                String newLineChar = "\n";
//                if (sector.getValue() != null) {
//                    if (sector.getValue().contains(newLineChar)) {
//                        String[] sectors = sector.getValue().split(newLineChar);
//                        for (String sectorString : sectors) {
//                            indexParty.addActivitySector(sector.getLanguageID(), sectorString);
//                        }
//                    } else {
//                        indexParty.addActivitySector(sector.getLanguageID(), sector.getValue());
//                    }
//                }
//            }
//        }
        if (party.getIndustryClassificationCode() != null) {
            indexParty.setBusinessType(party.getIndustryClassificationCode().getValue());
        }
        //adding logo id
        if (party.getDocumentReference() != null) {
            for (DocumentReferenceType documentReference : party.getDocumentReference()) {
                if (UblAdapter.DOCUMENT_TYPE_COMPANY_LOGO.equals(documentReference.getDocumentType())) {
                    indexParty.setLogoId(documentReference.getHjid().toString());
                    break;
                }
            }
        }

//        if(party.getWebsiteURI() != null) {
//            indexParty.setWebsite(party.getWebsiteURI());
//        }
//        indexParty.setVerified(false);
        return indexParty;
    }

}
