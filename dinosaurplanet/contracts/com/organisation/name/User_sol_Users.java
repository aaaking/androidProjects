package com.organisation.name;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class User_sol_Users extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506104d2806100206000396000f3006080604052600436106100605763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041662ce8e3e8114610065578063365b98b214610158578063a8293c281461018e578063b69ea684146101ba575b600080fd5b34801561007157600080fd5b5061007a6101ee565b60405180806020018060200180602001848103845287818151815260200191508051906020019060200280838360005b838110156100c25781810151838201526020016100aa565b50505050905001848103835286818151815260200191508051906020019060200280838360005b838110156101015781810151838201526020016100e9565b50505050905001848103825285818151815260200191508051906020019060200280838360005b83811015610140578181015183820152602001610128565b50505050905001965050505050505060405180910390f35b34801561016457600080fd5b50610170600435610353565b60408051938452602084019290925282820152519081900360600190f35b34801561019a57600080fd5b506101a6600435610384565b604080519115158252519081900360200190f35b3480156101c657600080fd5b506101d56004356024356103d4565b6040805192835290151560208301528051918290030190f35b6060806060600060608060606000610204610486565b600054604080518281526020808402820101909152909650868015610233578160200160208202803883390190505b50945085604051908082528060200260200182016040528015610260578160200160208202803883390190505b5093508560405190808252806020026020018201604052801561028d578160200160208202803883390190505b509250600091505b858210156103455760008054839081106102ab57fe5b60009182526020918290206040805160608101825260039093029091018054808452600182015494840194909452600201549082015286519092508690849081106102f257fe5b90602001906020020181815250508060200151848381518110151561031357fe5b602090810290910101526040810151835184908490811061033057fe5b60209081029091010152600190910190610295565b509297919650945092505050565b600080548290811061036157fe5b600091825260209091206003909102018054600182015460029092015490925083565b6000808281548110151561039457fe5b9060005260206000209060030201600201546005016000838154811015156103b857fe5b6000918252602090912060026003909202010155506001919050565b6000806103df610486565b600180548082018255808352602083019687526040830195865260008054808401825590805292517f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56360039094029384015595517f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56483015593517f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56590910155509192909150565b6040805160608101825260008082526020820181905291810191909152905600a165627a7a723058209e2b89f7ad826e1e9c944954e226fdde8ca70535b37ae8a267727b7afc7241320029";

    public static final String FUNC_GETUSERS = "getUsers";

    public static final String FUNC_USERS = "users";

    public static final String FUNC_PLUSFIVE = "plusFive";

    public static final String FUNC_ADDUSER = "addUser";

    protected User_sol_Users(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected User_sol_Users(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Tuple3<List<BigInteger>, List<byte[]>, List<BigInteger>>> getUsers() {
        final Function function = new Function(FUNC_GETUSERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Bytes32>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<Tuple3<List<BigInteger>, List<byte[]>, List<BigInteger>>>(
                new Callable<Tuple3<List<BigInteger>, List<byte[]>, List<BigInteger>>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<byte[]>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<byte[]>, List<BigInteger>>(
                                convertToNative((List<Uint256>) results.get(0).getValue()), 
                                convertToNative((List<Bytes32>) results.get(1).getValue()), 
                                convertToNative((List<Uint256>) results.get(2).getValue()));
                    }
                });
    }

    public RemoteCall<Tuple3<BigInteger, byte[], BigInteger>> users(BigInteger param0) {
        final Function function = new Function(FUNC_USERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, byte[], BigInteger>>(
                new Callable<Tuple3<BigInteger, byte[], BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, byte[], BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> plusFive(BigInteger id) {
        final Function function = new Function(
                FUNC_PLUSFIVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addUser(byte[] userName, BigInteger userPoint) {
        final Function function = new Function(
                FUNC_ADDUSER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userName), 
                new org.web3j.abi.datatypes.generated.Uint256(userPoint)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<User_sol_Users> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(User_sol_Users.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<User_sol_Users> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(User_sol_Users.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static User_sol_Users load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new User_sol_Users(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static User_sol_Users load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new User_sol_Users(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
