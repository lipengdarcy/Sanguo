package test.bitcoin;

import java.io.File;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

/**
 * 收取比特币
 */

public class BitCoinTest implements WalletCoinsReceivedEventListener {

	public static void main(String[] args) {
		BitCoinTest demo = new BitCoinTest();
		demo.run();
	}

	public void run() {
		try {
			// 1.调用init方法进行初始化
			init();
			System.out.println("Waiting for coins...");
			// 2.然后进入一个等待循环, 当有比特币到来时, onCoinsReceived方法就被触发.
			while (true) {
				Thread.sleep(20);
			}
		} catch (BlockStoreException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void init() throws BlockStoreException {
		/**
		 * 选择运行环境
		 * 
		 * 比特币应用可以在三种不同的环境中运行: 正式流通网络, 测试流通网络以及本地开发环境.
		 * 初始化的第一步是通过设置一个NetworkParameters变量来选择运行环境, 以下代码使用测试流通网络
		 */
		NetworkParameters params = TestNet3Params.get();

		// 获取地址和设置钱包对象,创建一个可用于接受比特币的地址, 并将其导入相应的钱包对象中.
		ECKey key = new ECKey();
		System.out.println("We created a new key:\n" + key);
		Address addressFromKey = key.toAddress(params);
		System.out.println("Public Address generated: " + addressFromKey);
		System.out.println("Private key is: " + key.getPrivateKeyEncoded(params).toString());
		Wallet wallet = new Wallet(params);
		wallet.importKey(key);

		// 接入流通网络并下载比特币区块
		File blockFile = new File("D:/bitcoin-blocks");
		SPVBlockStore blockStore = new SPVBlockStore(params, blockFile);

		BlockChain blockChain = new BlockChain(params, wallet, blockStore);
		PeerGroup peerGroup = new PeerGroup(params, blockChain);
		peerGroup.addPeerDiscovery(new DnsDiscovery(params));
		peerGroup.addWallet(wallet);

		System.out.println("Start peer group");
		peerGroup.start();

		System.out.println("Downloading block chain");
		peerGroup.downloadBlockChain();
		System.out.println("Block chain downloaded");

		// 设置当比特币到来时的事件响应
		wallet.addCoinsReceivedEventListener(this);
	}

	@Override
	public void onCoinsReceived(final Wallet wallet, final Transaction transaction, Coin prevBalance, Coin newBalance) {
		final Coin value = transaction.getValueSentToMe(wallet);

		System.out.println("Received tx for " + value.toFriendlyString() + ": " + transaction);
		System.out.println("Previous balance is " + prevBalance.toFriendlyString());
		System.out.println("New estimated balance is " + newBalance.toFriendlyString());
		System.out.println("Coin received, wallet balance is :" + wallet.getBalance());

		/**
		 * 当比特币到来时onCoinsReceived方法就会触发,newBalance参数提供的是钱包中金额的估计值,其实际金额要等到交易被网络确认后才会提现在wallet.getBalance()的返回值中
		 */
		Futures.addCallback(transaction.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
			public void onSuccess(TransactionConfidence result) {
				System.out.println("Transaction confirmed, wallet balance is :" + wallet.getBalance());
			}

			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}
}
