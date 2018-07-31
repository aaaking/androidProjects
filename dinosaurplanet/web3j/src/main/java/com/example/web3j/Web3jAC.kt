package com.example.web3j

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.ac_web3j.*
import android.widget.ArrayAdapter
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.admin.AdminFactory
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger


/**
 * Created by 周智慧 on 30/07/2018.
 */
val REQUEST_PERMISSION_WRITE_STORAGE = 0

fun startWeb3jAC(activity: Activity) {
    with(Intent(activity, Web3jAC::class.java)) {
        //        this.putExtras(activity.intent?.extras)
        activity.startActivity(this)
    }
}

class Web3jAC : AppCompatActivity() {
    var credentials: Credentials? = null
    var data_list = ArrayList<String>()
    var web3j: Web3j? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_web3j)
        //difference among these networks
        //https://ethereum.stackexchange.com/questions/27048/comparison-of-the-different-testnets
        data_list.add("https://mainnet.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://ropsten.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://kovan.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://rinkeby.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        var arr_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list)
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arr_adapter
        btn_Connect.setOnClickListener {
            Thread(Runnable {
                web3j = Web3jFactory.build(HttpService(spinner.selectedItem.toString()))
                val web3ClientVersion = web3j?.web3ClientVersion()?.send()
                val clientVersion = web3ClientVersion?.getWeb3ClientVersion()
                Log.i("zzh", clientVersion?.toString())
                runOnUiThread {
                    transfer.isEnabled = credentials != null && web3j != null
                    deploy.isEnabled = credentials != null && web3j != null
                    btn_call.isEnabled = credentials != null && web3j != null
                    btn_newGreeting.isEnabled = credentials != null && web3j != null
                    custom_transaction.isEnabled = credentials != null && web3j != null
                    status.text = if (web3j != null) "连接：" + clientVersion else "断开"
                }
            }).start()
        }
        //
        btn_GenerateWallet.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this@Web3jAC, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@Web3jAC, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_STORAGE)
            } else {
                // We then need to load our Ethereum wallet file
                // FIXME: Generate a new wallet file using the web3j command line tools https://docs.web3j.io/command_line.html
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!path.exists()) {
                    path.mkdir()
                }
                var password = "12345678"//pwd.text.toString()
                var fileName = "original" + ".json"//WalletUtils.generateLightNewWalletFile(password, File(path.toString()))
                Log.e("zzh", "generateWallet: $path/$fileName")
                credentials = WalletUtils.loadCredentials(password, path.toString() + "/" + fileName)
                Log.i("zzh", credentials!!.getAddress())
                wallet_address.text = credentials!!.getAddress()
                Log.i("zzh", credentials.toString())
                transfer.isEnabled = credentials != null && web3j != null
                deploy.isEnabled = credentials != null && web3j != null
                btn_call.isEnabled = credentials != null && web3j != null
                btn_newGreeting.isEnabled = credentials != null && web3j != null
                custom_transaction.isEnabled = credentials != null && web3j != null
            }
        }

        //transfer
        val transferThread = Thread(Runnable {
            // FIXME: Request some Ether for the Rinkeby test network at https://www.rinkeby.io/#faucet
            val transferReceipt = Transfer.sendFunds(web3j, credentials, "0x8717c17c23a44564a8a08510278b9b45074f8f23", BigDecimal.ONE, Convert.Unit.FINNEY).send()
            Log.i("zzh", "Transaction complete, view it at https://rinkeby.etherscan.io/tx/" + transferReceipt.transactionHash)
            runOnUiThread {
                txhash.text = "Transaction complete, view it at https://rinkeby.etherscan.io/tx/" + transferReceipt.transactionHash
            }
        })
        transfer.setOnClickListener {
            if (transferThread.getState() == Thread.State.NEW) {
                transferThread.start();
            }
        }

        //deploy
        val deployThread = Thread(Runnable {
            val contract = Greeter.deploy(web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, "Hello bipa!").send()
            val contractAddress = contract.contractAddress
            Log.i("zzh", contractAddress)
            Log.i("zzh", "View contract at https://rinkeby.etherscan.io/address/" + contractAddress)
            runOnUiThread {
                contract_address.text = contractAddress
            }
        })
        deploy.setOnClickListener {
            if (deployThread.getState() == Thread.State.NEW) {
                deployThread.start();
            }
        }

        //call contract function
        btn_call.setOnClickListener {
            Thread(Runnable {
                var myContract = Greeter.load("0xa2264d93ccee94044b71da856e6b09c4e728530e", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
                var content = myContract.greet().send()
                Log.i("zzh", "Value stored in remote smart contract: " + content)
                runOnUiThread {
                    old_params.setText(content)
                }
            }).start()
        }
        var newGreetingThread = Thread(Runnable {
            var myContract = Greeter.load("0xa2264d93ccee94044b71da856e6b09c4e728530e", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
            val transactionReceipt = myContract.newGreeting(new_params.text.toString()).send()
            // Events enable us to log specific events happening during the execution of our smart
            // contract to the blockchain. Index events cannot be logged in their entirety.
            // For Strings and arrays, the hash of values is provided, not the original value.
            // For further information, refer to https://docs.web3j.io/filters.html#filters-and-events
            for (event in myContract.getModifiedEvents(transactionReceipt)) {
                Log.i("zzh", "Modify event fired, previous value: " + event.oldGreeting + ", new value: " + event.newGreeting)
                Log.i("zzh", "Indexed event previous value: " + Numeric.toHexString(event.oldGreetingIdx) + ", new value: " + Numeric.toHexString(event.newGreetingIdx))
            }
        })
        btn_newGreeting.setOnClickListener {
            if (newGreetingThread.getState() == Thread.State.NEW) {
                newGreetingThread.start();
            }
        }
        // custom_transaction
        custom_transaction.setOnClickListener {
            Thread(Runnable {
                // get the next available nonce
                var ethGetTransactionCount = web3j!!.ethGetTransactionCount("0xc7B5F6d0245339674ae4264E44173bC606881651", DefaultBlockParameterName.LATEST).send();
                var nonce = ethGetTransactionCount.getTransactionCount();
                // create our transaction  RawTransaction
                var rawTransaction = RawTransaction.createEtherTransaction(nonce, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, "0x8717c17c23a44564a8a08510278b9b45074f8f23", BigInteger.valueOf(1e15.toLong()));
                // sign & send our transaction
                var signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                var hexValue = Numeric.toHexString(signedMessage);
                var ethSendTransaction = web3j!!.ethSendRawTransaction(hexValue).send();//EthSendTransaction
                Log.i("zzh", "https://rinkeby.etherscan.io/tx/" + ethSendTransaction.transactionHash)
            }).start()
        }
        //offline_sign
        offline_sign.setOnClickListener {
            Thread(Runnable {
                // get the next available nonce
//                var ethGetTransactionCount = web3j!!.ethGetTransactionCount("0xc7B5F6d0245339674ae4264E44173bC606881651", DefaultBlockParameterName.LATEST).send();
//                var nonce = ethGetTransactionCount.getTransactionCount();
//                // create our transaction  RawTransaction
//                var rawTransaction = RawTransaction.createEtherTransaction(nonce, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, "0x8717c17c23a44564a8a08510278b9b45074f8f23", BigInteger.valueOf(1e15.toLong()));
//                // sign & send our transaction
//                var signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                var hexValue = "0xf8aa1385098bca5a00830249f094122638aeaccdadb35a707c5ffcaa0226e43dc02b80b844a9059cbb0000000000000000000000008717c17c23a44564a8a08510278b9b45074f8f230000000000000000000000000000000000000000000000000de0b6b3a76400002ca09a26b080215c32262a3b9aa77579016db3e3f4bdbdd629a8af9a079d4fc315cda04d8b9c6bf54ab9a23588c6bc27bcb393dd3840c8e4be969486b246d334597b8e"
                var ethSendTransaction = web3j!!.ethSendRawTransaction(hexValue).send();//EthSendTransaction
                Log.i("zzh", "https://rinkeby.etherscan.io/tx/" + ethSendTransaction.transactionHash)
            }).start()
        }
        //
        btn_erc20_token.setOnClickListener { startERC20TokenAC(this@Web3jAC) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                }
            }
        }
    }
}