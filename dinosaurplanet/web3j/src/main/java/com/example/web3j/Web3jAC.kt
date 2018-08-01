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
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.*
import org.web3j.crypto.WalletUtils.loadBip39Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.admin.AdminFactory
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*


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

    //ETH转账签名
    fun signedEthTransactionData(to: String, //转账的钱包地址
                                 nonce: BigInteger,//获取到的交易次数
                                 gasPrice: BigInteger, //
                                 gasLimit: BigInteger, //
                                 value: Double, //转账的值
                                 credentials: Credentials): String {
        //把十进制的转换成ETH的Wei, 1ETH = 10^18 Wei
        val realValue = Convert.toWei(value.toString(), Convert.Unit.ETHER)
        val rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                realValue.toBigIntegerExact())
        //手续费= (gasPrice * gasLimit ) / 10^18 ether
        //使用TransactionEncoder对RawTransaction进行签名操作
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        //转换成0x开头的字符串
        return Numeric.toHexString(signedMessage)
    }

    //基于以太坊的代币转账签名
    fun signedContractTransactionData(contractAddress: String,//代币的智能合约地址
                                      toAdress: String,//对方的地址
                                      nonce: BigInteger,//获取到交易数量
                                      gasPrice: BigInteger,
                                      gasLimit: BigInteger,
                                      value: Double, decimal: Double,
                                      credentials: Credentials): String {
        //因为每个代币可以规定自己的小数位, 所以实际的转账值=数值 * 10^小数位
        val realValue = BigDecimal.valueOf(value * Math.pow(10.0, decimal))
        //0xa9059cbb代表某个代币的转账方法hex(transfer) + 对方的转账地址hex + 转账的值的hex
        val data = methodHeader("transfer(address,uint256)")//Params.Abi.transfer + // 0xa9059cbb
        Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(toAdress), 64) +
                Numeric.toHexStringNoPrefixZeroPadded(realValue.toBigInteger(), 64)
        val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                contractAddress,
                data)

        //使用TransactionEncoder对RawTransaction进行签名操作
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        //转换成0x开头的字符串
        return Numeric.toHexString(signedMessage)
    }

    fun methodHeader(method: String): String {
        val bytes = method.toByteArray()
        val bytes1 = org.web3j.crypto.Hash.sha3(bytes)
        val hex = Numeric.toHexString(bytes1, 0, 4, true)
        return hex
    }

    @Throws(IOException::class, CipherException::class)
    fun signContractTransaction(contractAddress: String,
                                to: String,
                                nonce: BigInteger,
                                gasPrice: BigInteger,
                                gasLimit: BigInteger,
                                amount: BigDecimal,
                                decimal: BigDecimal,
                                crenditial: Credentials,
                                password: String): String {
        val realValue = amount.multiply(decimal)
        val function = Function("transfer",
                Arrays.asList<Type<out Any>>(Address(to), Uint256(realValue.toBigInteger())),
                Arrays.asList<TypeReference<*>>())
        val data = FunctionEncoder.encode(function)
        val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                contractAddress,
                data)
        var signedMessage = TransactionEncoder.signMessage(rawTransaction, crenditial)
        return Numeric.toHexString(signedMessage);
    }

    //基于以太坊的代币转账签名2,这里我们提供另外一种web3j既有的封装实现,不用关心内部参数是如何拼接的.(推荐)
    @Throws(IOException::class, CipherException::class)
    fun signContractTransaction2(contractAddress: String,
                                to: String,
                                nonce: BigInteger,
                                gasPrice: BigInteger,
                                gasLimit: BigInteger,
                                amount: BigDecimal,
                                decimal: BigDecimal,
                                crenditial: Credentials,
                                password: String) {
        val realValue = amount.multiply(decimal)
        val function = Function("transfer",
                Arrays.asList<Type<out Any>>(Address(to), Uint256(realValue.toBigInteger())),
                Arrays.asList<TypeReference<*>>())
        val data = FunctionEncoder.encode(function)
        val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                contractAddress,
                data)

        val transaction = Transaction.createFunctionCallTransaction(
                to, nonce, gasPrice,
                gasLimit, contractAddress, BigInteger("0"),
                data)
        val transactionResponse = web3j!!.ethSendTransaction(transaction).sendAsync().get()
    }
}