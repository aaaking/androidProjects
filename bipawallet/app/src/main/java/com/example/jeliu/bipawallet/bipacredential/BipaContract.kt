package com.example.jeliu.bipawallet.bipacredential

import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Contract
import java.math.BigInteger
import java.util.*
import java.util.concurrent.Callable

/**
 * Created by 周智慧 on 2018/9/25.
 */
class BipaContract(contractBinary: String?, contractAddress: String?, web3j: Web3j?, credentials: Credentials?, gasPrice: BigInteger?, gasLimit: BigInteger?) : Contract(contractBinary, contractAddress, web3j, credentials, gasPrice, gasLimit) {
    fun callFunc(funcName: String): RemoteCall<String> {
        val function = Function(funcName,
                Arrays.asList(),
                Arrays.asList<TypeReference<*>>(object : TypeReference<Utf8String>() {

                }))
        return executeRemoteCallSingleValueReturn(function, String::class.java)
    }

    fun <T> executeRemoteCallSingleValueRet(function: Function, returnType: Class<T>): RemoteCall<T> {
        return super.executeRemoteCallSingleValueReturn(function, returnType)
    }

    fun executeRemoteCallTransactionBipa(function: Function): RemoteCall<TransactionReceipt> {
        return executeRemoteCallTransaction(function)
    }
}