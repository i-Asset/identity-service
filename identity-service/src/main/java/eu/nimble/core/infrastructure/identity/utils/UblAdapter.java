package eu.nimble.core.infrastructure.identity.utils;

import eu.nimble.core.infrastructure.identity.entity.dto.Address;
import eu.nimble.core.infrastructure.identity.entity.dto.CompanySettings;
import eu.nimble.core.infrastructure.identity.entity.dto.DeliveryTerms;
import eu.nimble.service.model.ubl.commonaggregatecomponents.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Johannes Innerbichler on 04/07/17.
 */
@SuppressWarnings("WeakerAccess")
public class UblAdapter {

    public static CompanySettings adaptCompanySettings(PartyType partyType) {
        CompanySettings settings = new CompanySettings();
        settings.setAddress(adaptAddress(partyType.getPostalAddress()));
        //noinspection ConstantConditions // TODO: check if terms exist
        settings.setDeliveryTerms(adaptDeliveryTerms(partyType.getDeliveryTerms().stream().findFirst().get()));

        return settings;
    }

    public static Address adaptAddress(AddressType ublAddress) {

        if (ublAddress == null)
            return new Address();

        Address dtoAddress = new Address();
        dtoAddress.setStreetName(ublAddress.getStreetName());
        dtoAddress.setBuildingNumber(ublAddress.getBuildingNumber());
        dtoAddress.setCityName(ublAddress.getCityName());
        dtoAddress.setPostalCode(ublAddress.getPostalZone());
        if (ublAddress.getCountry() != null)
            dtoAddress.setCountry(ublAddress.getCountry().getName());
        return dtoAddress;
    }

    public static AddressType adaptAddress(Address dtoAddress) {

        if (dtoAddress == null)
            return new AddressType();

        AddressType ublAddress = new AddressType();
        ublAddress.setStreetName(dtoAddress.getStreetName());
        ublAddress.setBuildingNumber(dtoAddress.getBuildingNumber());
        ublAddress.setCityName(dtoAddress.getCityName());
        ublAddress.setPostalZone(dtoAddress.getPostalCode());

        CountryType country = new CountryType();
        country.setName(dtoAddress.getCountry());
        ublAddress.setCountry(country);

        return ublAddress;
    }

    public static DeliveryTerms adaptDeliveryTerms(DeliveryTermsType ublDeliveryTerms) {

        if (ublDeliveryTerms == null)
            return new DeliveryTerms();

        DeliveryTerms dtoDeliveryTerms = new DeliveryTerms();
        dtoDeliveryTerms.setSpecialTerms(ublDeliveryTerms.getSpecialTerms());
        if (ublDeliveryTerms.getDelivery() != null) {
            dtoDeliveryTerms.setEstimatedDeliveryTime(ublDeliveryTerms.getDelivery().getActualDeliveryTime().getDay());
            dtoDeliveryTerms.setDeliveryAddress(adaptAddress(ublDeliveryTerms.getDelivery().getDeliveryAddress()));
        }

        return dtoDeliveryTerms;
    }

    public static DeliveryTermsType adaptDeliveryTerms(DeliveryTerms dtoDeliveryTerms) {

        DeliveryTermsType ublDeliveryTerms = new DeliveryTermsType();
        ublDeliveryTerms.setSpecialTerms(dtoDeliveryTerms.getSpecialTerms());

        DeliveryType delivery = new DeliveryType();
        delivery.setDeliveryAddress(adaptAddress(dtoDeliveryTerms.getDeliveryAddress()));

        // ToDO: create GregorianCalender and add 'EstimatedDeliveryTime'.

        return ublDeliveryTerms;
    }

    public static <V> List<V> toModifyableList(V... objects) {
        return new ArrayList<>(Arrays.asList(objects));
    }
}
