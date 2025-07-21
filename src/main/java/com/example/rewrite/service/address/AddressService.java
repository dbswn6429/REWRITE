package com.example.rewrite.service.address;

import com.example.rewrite.entity.Address;

import java.util.List;

public interface AddressService {


    public List<Address> getAddress(Long uid);
    public void addressWrite(Address address);
    public void checkDefault(Long addressId, Long uid);
    public void deleteAddress(Long addressId);
    public Address updateAddress(Long addressId);
    public void modifyAddress(Address address);
    Address getDefaultAddress(Long uid);

}
