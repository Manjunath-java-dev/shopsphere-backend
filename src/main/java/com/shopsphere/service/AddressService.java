package com.shopsphere.service;

import com.shopsphere.dto.request.AddressRequest;
import com.shopsphere.dto.response.AddressResponse;
import com.shopsphere.entity.Address;
import com.shopsphere.entity.User;
import com.shopsphere.exception.AddressNotFoundException;
import com.shopsphere.repositoy.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }

    //1 add address
    public AddressResponse addAddress(User user, AddressRequest addressRequest){

        // If this address should be default,
        // remove default from existing addresses
        if(addressRequest.isDefault()){
          List<Address> existingAddresses =  addressRepository.findByUser(user);
          for (Address address : existingAddresses){
              address.setDefault(false);
          }
          addressRepository.saveAll(existingAddresses);
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressLine1(addressRequest.getAddressLine1());
        address.setAddressLine2(addressRequest.getAddressLine2());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setPincode(addressRequest.getPincode());
        address.setCountry(addressRequest.getCountry());
        address.setAddressType(addressRequest.getAddressType());
        address.setDefault(addressRequest.isDefault());

      address =  addressRepository.save(address);

        return convertToResponse(address);

    }

    // 2. Get all my addresses
    public List<AddressResponse> getMyAddresses(User user){
      List<Address> addresses =  addressRepository.findByUser(user);
      List<AddressResponse> responses = new ArrayList<>();
      for (Address address : addresses){
          responses.add(convertToResponse(address));
      }
      return responses;
    }

    //3 get one address
    public AddressResponse getAddress(User user,Long addressId){
      Address address =  addressRepository.findByIdAndUser(addressId,user)
                .orElseThrow(()->new AddressNotFoundException("Address not found"));

      return convertToResponse(address);
    }

    // 4. Update address
    public AddressResponse updateAddress(
            User user,
            Long addressId,
            AddressRequest request) {

        Address address =
                addressRepository.findByIdAndUser(addressId, user)
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Address not found"));

        // If changing this address to default
        if (request.isDefault()) {

            List<Address> existingAddresses =
                    addressRepository.findByUser(user);

            for (Address existingAddress : existingAddresses) {
                existingAddress.setDefault(false);
            }

            addressRepository.saveAll(existingAddresses);
        }

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry());
        address.setAddressType(request.getAddressType());
        address.setDefault(request.isDefault());

        address = addressRepository.save(address);

        return convertToResponse(address);
    }

    // 5. Delete address
    public void deleteAddress(
            User user,
            Long addressId) {

        Address address =
                addressRepository.findByIdAndUser(addressId, user)
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Address not found"));

        addressRepository.delete(address);
    }

    // 6. Convert Entity → Response DTO
    private AddressResponse convertToResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .addressType(address.getAddressType())
                .isDefault(address.isDefault())
                .build();
    }
}
