package org.bitcoincashj.examples;

import lombok.extern.slf4j.Slf4j;
import org.bitcoincashj.core.Address;
import org.bitcoincashj.core.Coin;
import org.bitcoincashj.core.InsufficientMoneyException;
import org.bitcoincashj.core.NetworkParameters;
import org.bitcoincashj.kits.WalletAppKit;
import org.bitcoincashj.params.MainNetParams;
import org.bitcoincashj.params.TestNet3Params;
import org.bitcoincashj.wallet.DeterministicSeed;
import org.bitcoincashj.wallet.SendRequest;
import org.bitcoincashj.wallet.UnreadableWalletException;
import org.bitcoincashj.wallet.Wallet;
import org.junit.Test;

import java.io.File;


/**
 * @Author: xinyueyan
 * @Date: 11/15/2018 10:51 AM
 */
@Slf4j
public class Test1115 {
    /*NetworkParameters params = MainNetParams.get();
    String filePrefix = "chainddwallet-bch-mainnet";*/

    NetworkParameters params = TestNet3Params.get();
    String filePrefix = "chainddwallet-bch-testnet";

    //创建新钱包
    @Test
    public void createWallet() {
        //创建钱包，该方法会创建一个钱包文件 chainddwallet-btc-testnet.wallet。如果该文件已存在则不再创建
        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix);
        //同步区块头数据
        kit.startAsync();
        kit.awaitRunning();

        //获取钱包对象
        Wallet walelt = kit.wallet();
        log.info("===wallet.toString===" + walelt.toString());
        log.info("===wallet.getBalance===" + walelt.getBalance());
        //device kitten plunge lemon that daughter armor board require such tent lens
        log.info("===getMnemonicCode===" + walelt.getKeyChainSeed().getMnemonicCode());
        log.info("===currentReceiveAddress===" + walelt.currentReceiveAddress().toString());
    }

    //从助记词恢复钱包
    @Test
    public void restoreFromSeed(){
        //公链助记词
        //String seedCode = "jacket arm language relax meadow that ghost swallow early coast magic coil";
        //测试链助记词1---1115创建
        String seedCode = "bottom olympic involve puppy nominee curtain target lecture excuse hand comic liar";
        //String seedCode = "trumpet saddle teach test best borrow rigid frequent federal spy donkey mask";
        String passphrase = "";
        //Long creationTime = 1539828300L;
        Long creationTime = 1539828300L;
        DeterministicSeed seed = null;
        try {
            seed = new DeterministicSeed(seedCode, null, passphrase, creationTime);
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }

        WalletAppKit walletAppKit = new WalletAppKit(params, new File("./newseed"), "bchmain");
        walletAppKit = walletAppKit.restoreWalletFromSeed(seed);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        Wallet wallet = walletAppKit.wallet();
        log.info("wallet=" + wallet.toString());
        log.info("currentReceiveAddress=" + wallet.currentReceiveAddress());
        log.info("getBalance=" + wallet.getBalance());
    }


    //查询钱包余额
    @Test
    public void getBalance(){
        //===begin 方法一：该方法可以保证钱包余额的实时更新，但需要联网同步块数据，速度慢
        //WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix);
        WalletAppKit kit = new WalletAppKit(params, new File("./newseed"), "bchmain");

        //同步区块头数据
        kit.startAsync();
        kit.awaitRunning();
        Wallet wallet = kit.wallet();
        //===end 方法一


        //====begin 方法二：该方法速度快，但无法获取最新的钱包余额
       /* Wallet wallet = null;
        try {
            wallet = Wallet.loadFromFile(new File("./chainddwallet-btc-testnet.wallet"));
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }*/
        //===end 方法二

        log.info("wallet=" + wallet.toString());
        log.info("currentReceiveAddress=" + wallet.currentReceiveAddress());
        log.info("getBalance=" + wallet.getBalance());
    }

    //转账
    @Test
    public void sendBch(){
        //WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix);
        WalletAppKit kit = new WalletAppKit(params, new File("./newseed"), "bchmain");
        //同步区块头数据
        kit.startAsync();
        kit.awaitRunning();
        Wallet wallet = kit.wallet();

        log.info("===currentReceiveAddress===" + wallet.currentReceiveAddress().toString());
        log.info("===getBalance===" + wallet.getBalance().value);
        //Address targetAddress  = Address.fromBase58(params, "1BA1ddKbZ4UEr41w1ojtLv4BNfLmXBr2iT");
        Address targetAddress  = Address.fromBase58(params, "mpZTfeyd518cPqVsSEm5fDKBw68RWFx8BU");
        //Address targetAddress  = Address.fromBase58(params, "myfvt8nK5ioEzcAqAA7JxBVugnE6tbnFaC");

        try {
            //300000是转账金额，单位是聪，1btc=1E8聪
            SendRequest sendRequest = SendRequest.to(targetAddress, Coin.valueOf(2000000));
            //设置矿工费，单位为聪，如果不设置则默认手续费为 100000聪
            //另外，矿工费可以从第三方网站实时获取，参考 https://bitcoinfees.earn.com/api/v1/fees/recommended
            sendRequest.feePerKb = Coin.parseCoin("0.0005");
            Wallet.SendResult sendResult = wallet.sendCoins(sendRequest);
            log.info("===getFee===" + sendResult.tx.getFee());//该笔交易花费的手续费
            log.info("===getHashAsString===" + sendResult.tx.getHashAsString());//交易hash
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
    }
}
