package com.paytx.api.multiverxcore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import multiversx.Account;
import multiversx.Address;
import multiversx.ProxyProvider;
import multiversx.Wallet;
import multiversx.Exceptions.AddressException;
import multiversx.Exceptions.CannotDeriveKeysException;

@Component
public class Merchant {
    
    private final Wallet wallet;
    private final Address address;
    private final Account account;
    private final ProxyProvider provider;
    private final Address payTxAddress;
    
    @Autowired
    public Merchant(@Value("${multiversx.wallet.mnemonic}") String memonic,
                    @Value("${multiversx.account.address}") String address,
                    @Value("${multiversx.account.payTxAddress}") String payTxAddress,
                    @Value("${multiversx.provider.url}") String providerUrl
            ) throws CannotDeriveKeysException, AddressException{
        this.wallet = Wallet.deriveFromMnemonic(memonic, 0);
        this.address = Address.fromBech32(address);
        this.payTxAddress = Address.fromBech32(payTxAddress);
        this.account = new Account(this.address);
        this.provider = new ProxyProvider(providerUrl); 
        System.out.println(memonic);       
    }

    public Wallet getWallet(){
        return this.wallet;
    }
    
    public Address getAddress(){
        return this.address;
    }

    public Address getPayTxAddress(){
        return this.payTxAddress;
    }

    public Account getAccount(){
        return this.account;
    }

    public ProxyProvider getProvider(){
        return this.provider;
    }
}
