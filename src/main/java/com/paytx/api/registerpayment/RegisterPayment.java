package com.paytx.api.registerpayment;

import multiversx.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;

@RestController
public class RegisterPayment {
    @GetMapping("/registerpayment")
    @ResponseBody
    public String registerPayment(@RequestParam String paymentAmount) throws Exceptions.AddressException, Exceptions.CannotSignTransactionException, Exceptions.CannotSerializeTransactionException, Exceptions.ProxyRequestException, IOException, Exceptions.CannotDeriveKeysException {
        Transaction transaction = new Transaction();
        String privateKey = "TODO";
        byte[] privateKeyBytes = privateKey.getBytes();
        Wallet wallet = new Wallet(privateKeyBytes);
        Address senderAddress = Address.fromBech32("erd1k5jt6umyca9090xvy9fsjuaunur2qqaac6zrdyqqvwd9jza8uhss7jgred");
        Account account = new Account(senderAddress);
        ProxyProvider devnetProvider = new ProxyProvider("https://devnet-gateway.elrond.com");

        account.sync(devnetProvider);
        transaction.setNonce(account.getNonce());
        transaction.setValue(new BigInteger(paymentAmount));
        transaction.setSender(senderAddress);
        transaction.setReceiver(Address.fromBech32("erd1qqqqqqqqqqqqqpgqknatrzgys8rmknnjxrn0vjue5sc5g4gnjpgsjeknhz"));
        transaction.setGasPrice(1_000_000_000);
        transaction.setGasLimit(2_000_000);
        transaction.setChainID("D");
        transaction.sign(wallet);

        String response = transaction.computeHash();
        System.out.println(transaction.serialize());
        transaction.send(devnetProvider);

        return response;
    }
}
