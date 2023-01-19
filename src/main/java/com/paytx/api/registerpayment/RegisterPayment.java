package com.paytx.api.registerpayment;

import multiversx.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.paytx.api.multiverxcore.Merchant;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class RegisterPayment {

    private final Merchant merchant;
    private final String payTxMethod;

    @Autowired
    public RegisterPayment(Merchant merchant, 
            @Value("${multiversx.payTx.method}") String payTxMethod){
        this.merchant = merchant;
        this.payTxMethod = payTxMethod;
    }

    @GetMapping("/registerpayment")
    @ResponseBody
    public HashMap<String, String> registerPayment(@RequestParam BigInteger paymentAmount) throws Exceptions.AddressException, Exceptions.CannotSignTransactionException, Exceptions.CannotSerializeTransactionException, Exceptions.ProxyRequestException, IOException, Exceptions.CannotDeriveKeysException {
        Transaction transaction = new Transaction();
        Account account = merchant.getAccount();
        IProvider provider = merchant.getProvider();
        account.sync(provider);
        transaction.setNonce(account.getNonce());
        transaction.setSender(merchant.getAddress());
        transaction.setReceiver(merchant.getPayTxAddress());
        transaction.setGasPrice(1_000_000_000);
        transaction.setGasLimit(2_000_000);
        transaction.setChainID("D");
        String paymentId = getRandomPaymentId();
        transaction.setData(payTxMethod+
                            "@"+
                            padEvenlyHex(paymentId) +
                            "@"+
                            padEvenlyHex(paymentAmount.toString(16)));
        transaction.sign(merchant.getWallet());
        HashMap<String, String> response = new HashMap<>();
        response.put("paymentId", padEvenlyHex(paymentId));
        response.put("transactionHash", transaction.computeHash());
        response.put("amountHex", padEvenlyHex(paymentAmount.toString(16)));
        response.put("amount", paymentAmount.toString());
        transaction.send(provider);
        return response;
    }

    private String padEvenlyHex(String hexString){
        String result = hexString;
        if(hexString.length() % 2 != 0){
            result = "0" + hexString;
        }
        return result;
    }

    private String getRandomPaymentId(){
        Integer paymentIdInteger = ThreadLocalRandom.current().nextInt();
        return Integer.toHexString(paymentIdInteger);
    }
}
