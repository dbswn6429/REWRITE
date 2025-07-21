package com.example.rewrite.service.address;


import com.example.rewrite.entity.Address;
import com.example.rewrite.repository.address.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;

    //주소지 리스트
    @Override
    public List<Address> getAddress(Long uid) {
        return addressRepository.getAddress(uid);
    }

    //주소지 추가
    @Override
    public void addressWrite(Address address) {
        addressRepository.save(address);
    }

    //기본 주소지 체크
    @Override
    public void checkDefault(Long addressId, Long uid) {
        addressRepository.checkDefault(addressId, uid);
    }

    //주소지 수정
    @Override
    public Address updateAddress(Long addressId) {
        return addressRepository.findByAddressId(addressId);
    }

    //주소지 삭제
    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteAddress(addressId);
    }

    @Override
    public void modifyAddress(Address address) {
        addressRepository.modifyAddress(address);
    }

    @Override
    public Address getDefaultAddress(Long uid) {
        List<Address> addressList = addressRepository.getAddress(uid);

        for(Address address : addressList) {
            if("C".equals(address.getIsDefault())) {
                return address;
            }
        }
        return null;
    }
}
