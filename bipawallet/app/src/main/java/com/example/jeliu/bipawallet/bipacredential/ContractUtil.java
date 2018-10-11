package com.example.jeliu.bipawallet.bipacredential;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes10;
import org.web3j.abi.datatypes.generated.Bytes11;
import org.web3j.abi.datatypes.generated.Bytes12;
import org.web3j.abi.datatypes.generated.Bytes13;
import org.web3j.abi.datatypes.generated.Bytes14;
import org.web3j.abi.datatypes.generated.Bytes15;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.abi.datatypes.generated.Bytes17;
import org.web3j.abi.datatypes.generated.Bytes18;
import org.web3j.abi.datatypes.generated.Bytes19;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Bytes20;
import org.web3j.abi.datatypes.generated.Bytes21;
import org.web3j.abi.datatypes.generated.Bytes22;
import org.web3j.abi.datatypes.generated.Bytes23;
import org.web3j.abi.datatypes.generated.Bytes24;
import org.web3j.abi.datatypes.generated.Bytes25;
import org.web3j.abi.datatypes.generated.Bytes26;
import org.web3j.abi.datatypes.generated.Bytes27;
import org.web3j.abi.datatypes.generated.Bytes28;
import org.web3j.abi.datatypes.generated.Bytes29;
import org.web3j.abi.datatypes.generated.Bytes3;
import org.web3j.abi.datatypes.generated.Bytes30;
import org.web3j.abi.datatypes.generated.Bytes31;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Bytes5;
import org.web3j.abi.datatypes.generated.Bytes6;
import org.web3j.abi.datatypes.generated.Bytes7;
import org.web3j.abi.datatypes.generated.Bytes8;
import org.web3j.abi.datatypes.generated.Bytes9;
import org.web3j.abi.datatypes.generated.Int104;
import org.web3j.abi.datatypes.generated.Int112;
import org.web3j.abi.datatypes.generated.Int120;
import org.web3j.abi.datatypes.generated.Int128;
import org.web3j.abi.datatypes.generated.Int136;
import org.web3j.abi.datatypes.generated.Int144;
import org.web3j.abi.datatypes.generated.Int152;
import org.web3j.abi.datatypes.generated.Int16;
import org.web3j.abi.datatypes.generated.Int160;
import org.web3j.abi.datatypes.generated.Int168;
import org.web3j.abi.datatypes.generated.Int176;
import org.web3j.abi.datatypes.generated.Int184;
import org.web3j.abi.datatypes.generated.Int192;
import org.web3j.abi.datatypes.generated.Int200;
import org.web3j.abi.datatypes.generated.Int208;
import org.web3j.abi.datatypes.generated.Int216;
import org.web3j.abi.datatypes.generated.Int224;
import org.web3j.abi.datatypes.generated.Int232;
import org.web3j.abi.datatypes.generated.Int24;
import org.web3j.abi.datatypes.generated.Int240;
import org.web3j.abi.datatypes.generated.Int248;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Int32;
import org.web3j.abi.datatypes.generated.Int40;
import org.web3j.abi.datatypes.generated.Int48;
import org.web3j.abi.datatypes.generated.Int56;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.Int72;
import org.web3j.abi.datatypes.generated.Int8;
import org.web3j.abi.datatypes.generated.Int80;
import org.web3j.abi.datatypes.generated.Int88;
import org.web3j.abi.datatypes.generated.Int96;
import org.web3j.abi.datatypes.generated.Uint104;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint120;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint136;
import org.web3j.abi.datatypes.generated.Uint144;
import org.web3j.abi.datatypes.generated.Uint152;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.abi.datatypes.generated.Uint168;
import org.web3j.abi.datatypes.generated.Uint176;
import org.web3j.abi.datatypes.generated.Uint184;
import org.web3j.abi.datatypes.generated.Uint192;
import org.web3j.abi.datatypes.generated.Uint200;
import org.web3j.abi.datatypes.generated.Uint208;
import org.web3j.abi.datatypes.generated.Uint216;
import org.web3j.abi.datatypes.generated.Uint224;
import org.web3j.abi.datatypes.generated.Uint232;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Uint240;
import org.web3j.abi.datatypes.generated.Uint248;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint40;
import org.web3j.abi.datatypes.generated.Uint48;
import org.web3j.abi.datatypes.generated.Uint56;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint72;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.abi.datatypes.generated.Uint80;
import org.web3j.abi.datatypes.generated.Uint88;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 周智慧 on 2018/9/25.
 */
public class ContractUtil {
    public static String methodHeader(String method) {
        byte[] bytes = method.getBytes();
        byte[] cryptedBytes = org.web3j.crypto.Hash.sha3(bytes);
        return Numeric.toHexString(cryptedBytes);
    }

    public static List<TypeReference<?>> getOutputs(String[] types) {
        List result = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (type.equals("address")) {
                result.add(new TypeReference<Address>() {
                });
            } else if (type.equals("bool")) {
                result.add(new TypeReference<Bool>() {
                });
            } else if (type.equals("string")) {
                result.add(new TypeReference<Utf8String>() {
                });
            } else if (type.equals("bytes")) {
                result.add(new TypeReference<DynamicBytes>() {
                });
            } else if (type.equals("uint")) {
                result.add(new TypeReference<Uint256>() {
                });
            } else if (type.equals("int")) {
                result.add(new TypeReference<Int256>() {
                });
            } else if (type.equals("uint8")) {
                result.add(new TypeReference<Uint8>() {
                });
            } else if (type.equals("int8")) {
                result.add(new TypeReference<Int8>() {
                });
            } else if (type.equals("uint16")) {
                result.add(new TypeReference<Uint16>() {
                });
            } else if (type.equals("int16")) {
                result.add(new TypeReference<Int16>() {
                });
            } else if (type.equals("uint24")) {
                result.add(new TypeReference<Uint24>() {
                });
            } else if (type.equals("int24")) {
                result.add(new TypeReference<Int24>() {
                });
            } else if (type.equals("uint32")) {
                result.add(new TypeReference<Uint32>() {
                });
            } else if (type.equals("int32")) {
                result.add(new TypeReference<Int32>() {
                });
            } else if (type.equals("uint40")) {
                result.add(new TypeReference<Uint40>() {
                });
            } else if (type.equals("int40")) {
                result.add(new TypeReference<Int40>() {
                });
            } else if (type.equals("uint48")) {
                result.add(new TypeReference<Uint48>() {
                });
            } else if (type.equals("int48")) {
                result.add(new TypeReference<Int48>() {
                });
            } else if (type.equals("uint56")) {
                result.add(new TypeReference<Uint56>() {
                });
            } else if (type.equals("int56")) {
                result.add(new TypeReference<Int56>() {
                });
            } else if (type.equals("uint64")) {
                result.add(new TypeReference<Uint64>() {
                });
            } else if (type.equals("int64")) {
                result.add(new TypeReference<Int64>() {
                });
            } else if (type.equals("uint72")) {
                result.add(new TypeReference<Uint72>() {
                });
            } else if (type.equals("int72")) {
                result.add(new TypeReference<Int72>() {
                });

            } else if (type.equals("uint80")) {
                result.add(new TypeReference<Uint80>() {
                });
            } else if (type.equals("int80")) {
                result.add(new TypeReference<Int80>() {
                });
            } else if (type.equals("uint88")) {
                result.add(new TypeReference<Uint88>() {
                });
            } else if (type.equals("int88")) {
                result.add(new TypeReference<Int88>() {
                });
            } else if (type.equals("uint96")) {
                result.add(new TypeReference<Uint96>() {
                });
            } else if (type.equals("int96")) {
                result.add(new TypeReference<Int96>() {
                });
            } else if (type.equals("uint104")) {
                result.add(new TypeReference<Uint104>() {
                });
            } else if (type.equals("int104")) {
                result.add(new TypeReference<Int104>() {
                });
            } else if (type.equals("uint112")) {
                result.add(new TypeReference<Uint112>() {
                });
            } else if (type.equals("int112")) {
                result.add(new TypeReference<Int112>() {
                });
            } else if (type.equals("uint120")) {
                result.add(new TypeReference<Uint120>() {
                });
            } else if (type.equals("int120")) {
                result.add(new TypeReference<Int120>() {
                });
            } else if (type.equals("uint128")) {
                result.add(new TypeReference<Uint128>() {
                });
            } else if (type.equals("int128")) {
                result.add(new TypeReference<Int128>() {
                });
            } else if (type.equals("uint136")) {
                result.add(new TypeReference<Uint136>() {
                });
            } else if (type.equals("int136")) {
                result.add(new TypeReference<Int136>() {
                });
            } else if (type.equals("uint144")) {
                result.add(new TypeReference<Uint144>() {
                });
            } else if (type.equals("int144")) {
                result.add(new TypeReference<Int144>() {
                });
            } else if (type.equals("uint152")) {
                result.add(new TypeReference<Uint152>() {
                });
            } else if (type.equals("int152")) {
                result.add(new TypeReference<Int152>() {
                });
            } else if (type.equals("uint160")) {
                result.add(new TypeReference<Uint160>() {
                });
            } else if (type.equals("int160")) {
                result.add(new TypeReference<Int160>() {
                });
            } else if (type.equals("uint168")) {
                result.add(new TypeReference<Uint168>() {
                });
            } else if (type.equals("int168")) {
                result.add(new TypeReference<Int168>() {
                });
            } else if (type.equals("uint176")) {
                result.add(new TypeReference<Uint176>() {
                });
            } else if (type.equals("int176")) {
                result.add(new TypeReference<Int176>() {
                });
            } else if (type.equals("uint184")) {
                result.add(new TypeReference<Uint184>() {
                });
            } else if (type.equals("int184")) {
                result.add(new TypeReference<Int184>() {
                });
            } else if (type.equals("uint192")) {
                result.add(new TypeReference<Uint192>() {
                });
            } else if (type.equals("int192")) {
                result.add(new TypeReference<Int192>() {
                });
            } else if (type.equals("uint200")) {
                result.add(new TypeReference<Uint200>() {
                });
            } else if (type.equals("int200")) {
                result.add(new TypeReference<Int200>() {
                });
            } else if (type.equals("uint208")) {
                result.add(new TypeReference<Uint208>() {
                });
            } else if (type.equals("int208")) {
                result.add(new TypeReference<Int208>() {
                });
            } else if (type.equals("uint216")) {
                result.add(new TypeReference<Uint216>() {
                });
            } else if (type.equals("int216")) {
                result.add(new TypeReference<Int216>() {
                });
            } else if (type.equals("uint224")) {
                result.add(new TypeReference<Uint224>() {
                });
            } else if (type.equals("int224")) {
                result.add(new TypeReference<Int224>() {
                });
            } else if (type.equals("uint232")) {
                result.add(new TypeReference<Uint232>() {
                });
            } else if (type.equals("int232")) {
                result.add(new TypeReference<Int232>() {
                });
            } else if (type.equals("uint240")) {
                result.add(new TypeReference<Uint240>() {
                });
            } else if (type.equals("int240")) {
                result.add(new TypeReference<Int240>() {
                });
            } else if (type.equals("uint248")) {
                result.add(new TypeReference<Uint248>() {
                });
            } else if (type.equals("int248")) {
                result.add(new TypeReference<Int248>() {
                });
            } else if (type.equals("uint256")) {
                result.add(new TypeReference<Uint256>() {
                });
            } else if (type.equals("int256")) {
                result.add(new TypeReference<Int256>() {
                });
            } else if (type.equals("bytes1")) {
                result.add(new TypeReference<Bytes1>() {
                });
            } else if (type.equals("bytes2")) {
                result.add(new TypeReference<Bytes2>() {
                });
            } else if (type.equals("bytes3")) {
                result.add(new TypeReference<Bytes3>() {
                });
            } else if (type.equals("bytes4")) {
                result.add(new TypeReference<Bytes4>() {
                });
            } else if (type.equals("bytes5")) {
                result.add(new TypeReference<Bytes5>() {
                });
            } else if (type.equals("bytes6")) {
                result.add(new TypeReference<Bytes6>() {
                });
            } else if (type.equals("bytes7")) {
                result.add(new TypeReference<Bytes7>() {
                });
            } else if (type.equals("bytes8")) {
                result.add(new TypeReference<Bytes8>() {
                });
            } else if (type.equals("bytes9")) {
                result.add(new TypeReference<Bytes9>() {
                });
            } else if (type.equals("bytes10")) {
                result.add(new TypeReference<Bytes10>() {
                });
            } else if (type.equals("bytes11")) {
                result.add(new TypeReference<Bytes11>() {
                });
            } else if (type.equals("bytes12")) {
                result.add(new TypeReference<Bytes12>() {
                });
            } else if (type.equals("bytes13")) {
                result.add(new TypeReference<Bytes13>() {
                });
            } else if (type.equals("bytes14")) {
                result.add(new TypeReference<Bytes14>() {
                });
            } else if (type.equals("bytes15")) {
                result.add(new TypeReference<Bytes15>() {
                });
            } else if (type.equals("bytes16")) {
                result.add(new TypeReference<Bytes16>() {
                });
            } else if (type.equals("bytes17")) {
                result.add(new TypeReference<Bytes17>() {
                });
            } else if (type.equals("bytes18")) {
                result.add(new TypeReference<Bytes18>() {
                });
            } else if (type.equals("bytes19")) {
                result.add(new TypeReference<Bytes19>() {
                });
            } else if (type.equals("bytes20")) {
                result.add(new TypeReference<Bytes20>() {
                });
            } else if (type.equals("bytes21")) {
                result.add(new TypeReference<Bytes21>() {
                });
            } else if (type.equals("bytes22")) {
                result.add(new TypeReference<Bytes22>() {
                });
            } else if (type.equals("bytes23")) {
                result.add(new TypeReference<Bytes23>() {
                });
            } else if (type.equals("bytes24")) {
                result.add(new TypeReference<Bytes24>() {
                });
            } else if (type.equals("bytes25")) {
                result.add(new TypeReference<Bytes25>() {
                });
            } else if (type.equals("bytes26")) {
                result.add(new TypeReference<Bytes26>() {
                });
            } else if (type.equals("bytes27")) {
                result.add(new TypeReference<Bytes27>() {
                });
            } else if (type.equals("bytes28")) {
                result.add(new TypeReference<Bytes28>() {
                });
            } else if (type.equals("bytes29")) {
                result.add(new TypeReference<Bytes29>() {
                });
            } else if (type.equals("bytes30")) {
                result.add(new TypeReference<Bytes30>() {
                });
            } else if (type.equals("bytes31")) {
                result.add(new TypeReference<Bytes31>() {
                });
            } else if (type.equals("bytes32")) {
                result.add(new TypeReference<Bytes32>() {
                });
            } else {
                throw new UnsupportedOperationException("Unsupported type encountered: " + type);
            }
        }
        return result;
    }

    public static List<Type> getInputs(String[] types, String[] values) throws Exception {
        if (types.length != values.length) {
            throw new Exception("");
        }
        List result = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            String value = values[i];
            if (type.equals("address")) {
                result.add(new Address(value));
            } else if (type.equals("bool")) {
                result.add(new Bool(Boolean.parseBoolean(value)));
            } else if (type.equals("string")) {
                result.add(new Utf8String(value));
            } else if (type.equals("bytes")) {
                result.add(new DynamicBytes(value.getBytes()));
            } else if (type.equals("uint")) {
                result.add(new Uint256(new BigInteger(value)));
            } else if (type.equals("int")) {
                result.add(new Int256(new BigInteger(value)));
            } else if (type.equals("uint8")) {
                result.add(new Uint8(new BigInteger(value)));
            } else if (type.equals("int8")) {
                result.add(new Int8(new BigInteger(value)));
            } else if (type.equals("uint16")) {
                result.add(new Uint16(new BigInteger(value)));
            } else if (type.equals("int16")) {
                result.add(new Int16(new BigInteger(value)));
            } else if (type.equals("uint24")) {
                result.add(new Uint24(new BigInteger(value)));
            } else if (type.equals("int24")) {
                result.add(new Int24(new BigInteger(value)));
            } else if (type.equals("uint32")) {
                result.add(new Uint32(new BigInteger(value)));
            } else if (type.equals("int32")) {
                result.add(new Int32(new BigInteger(value)));
            } else if (type.equals("uint40")) {
                result.add(new Uint40(new BigInteger(value)));
            } else if (type.equals("int40")) {
                result.add(new Int40(new BigInteger(value)));
            } else if (type.equals("uint48")) {
                result.add(new Uint48(new BigInteger(value)));
            } else if (type.equals("int48")) {
                result.add(new Int48(new BigInteger(value)));
            } else if (type.equals("uint56")) {
                result.add(new Uint56(new BigInteger(value)));
            } else if (type.equals("int56")) {
                result.add(new Int56(new BigInteger(value)));
            } else if (type.equals("uint64")) {
                result.add(new Uint64(new BigInteger(value)));
            } else if (type.equals("int64")) {
                result.add(new Int64(new BigInteger(value)));
            } else if (type.equals("uint72")) {
                result.add(new Uint72(new BigInteger(value)));
            } else if (type.equals("int72")) {
                result.add(new Int72(new BigInteger(value)));
            } else if (type.equals("uint80")) {
                result.add(new Uint80(new BigInteger(value)));
            } else if (type.equals("int80")) {
                result.add(new Int80(new BigInteger(value)));
            } else if (type.equals("uint88")) {
                result.add(new Uint88(new BigInteger(value)));
            } else if (type.equals("int88")) {
                result.add(new Int88(new BigInteger(value)));
            } else if (type.equals("uint96")) {
                result.add(new Uint96(new BigInteger(value)));
            } else if (type.equals("int96")) {
                result.add(new Int96(new BigInteger(value)));
            } else if (type.equals("uint104")) {
                result.add(new Uint104(new BigInteger(value)));
            } else if (type.equals("int104")) {
                result.add(new Int104(new BigInteger(value)));
            } else if (type.equals("uint112")) {
                result.add(new Uint112(new BigInteger(value)));
            } else if (type.equals("int112")) {
                result.add(new Int112(new BigInteger(value)));
            } else if (type.equals("uint120")) {
                result.add(new Uint120(new BigInteger(value)));
            } else if (type.equals("int120")) {
                result.add(new Int120(new BigInteger(value)));
            } else if (type.equals("uint128")) {
                result.add(new Uint128(new BigInteger(value)));
            } else if (type.equals("int128")) {
                result.add(new Int128(new BigInteger(value)));
            } else if (type.equals("uint136")) {
                result.add(new Uint136(new BigInteger(value)));
            } else if (type.equals("int136")) {
                result.add(new Int136(new BigInteger(value)));
            } else if (type.equals("uint144")) {
                result.add(new Uint144(new BigInteger(value)));
            } else if (type.equals("int144")) {
                result.add(new Int144(new BigInteger(value)));
            } else if (type.equals("uint152")) {
                result.add(new Uint152(new BigInteger(value)));
            } else if (type.equals("int152")) {
                result.add(new Int152(new BigInteger(value)));
            } else if (type.equals("uint160")) {
                result.add(new Uint160(new BigInteger(value)));
            } else if (type.equals("int160")) {
                result.add(new Int160(new BigInteger(value)));
            } else if (type.equals("uint168")) {
                result.add(new Uint168(new BigInteger(value)));
            } else if (type.equals("int168")) {
                result.add(new Int168(new BigInteger(value)));
            } else if (type.equals("uint176")) {
                result.add(new Uint176(new BigInteger(value)));
            } else if (type.equals("int176")) {
                result.add(new Int176(new BigInteger(value)));
            } else if (type.equals("uint184")) {
                result.add(new Uint184(new BigInteger(value)));
            } else if (type.equals("int184")) {
                result.add(new Int184(new BigInteger(value)));
            } else if (type.equals("uint192")) {
                result.add(new Uint192(new BigInteger(value)));
            } else if (type.equals("int192")) {
                result.add(new Int192(new BigInteger(value)));
            } else if (type.equals("uint200")) {
                result.add(new Uint200(new BigInteger(value)));
            } else if (type.equals("int200")) {
                result.add(new Int200(new BigInteger(value)));
            } else if (type.equals("uint208")) {
                result.add(new Uint208(new BigInteger(value)));
            } else if (type.equals("int208")) {
                result.add(new Int208(new BigInteger(value)));
            } else if (type.equals("uint216")) {
                result.add(new Uint216(new BigInteger(value)));
            } else if (type.equals("int216")) {
                result.add(new Int216(new BigInteger(value)));
            } else if (type.equals("uint224")) {
                result.add(new Uint224(new BigInteger(value)));
            } else if (type.equals("int224")) {
                result.add(new Int224(new BigInteger(value)));
            } else if (type.equals("uint232")) {
                result.add(new Uint232(new BigInteger(value)));
            } else if (type.equals("int232")) {
                result.add(new Int232(new BigInteger(value)));
            } else if (type.equals("uint240")) {
                result.add(new Uint240(new BigInteger(value)));
            } else if (type.equals("int240")) {
                result.add(new Int240(new BigInteger(value)));
            } else if (type.equals("uint248")) {
                result.add(new Uint248(new BigInteger(value)));
            } else if (type.equals("int248")) {
                result.add(new Int248(new BigInteger(value)));
            } else if (type.equals("uint256")) {
                result.add(new Uint256(new BigInteger(value)));
            } else if (type.equals("int256")) {
                result.add(new Int256(new BigInteger(value)));
            } else if (type.equals("bytes1")) {
                result.add(new Bytes1(value.getBytes()));
            } else if (type.equals("bytes2")) {
                result.add(new Bytes2(value.getBytes()));
            } else if (type.equals("bytes3")) {
                result.add(new Bytes3(value.getBytes()));
            } else if (type.equals("bytes4")) {
                result.add(new Bytes4(value.getBytes()));
            } else if (type.equals("bytes5")) {
                result.add(new Bytes5(value.getBytes()));
            } else if (type.equals("bytes6")) {
                result.add(new Bytes6(value.getBytes()));
            } else if (type.equals("bytes7")) {
                result.add(new Bytes7(value.getBytes()));
            } else if (type.equals("bytes8")) {
                result.add(new Bytes8(value.getBytes()));
            } else if (type.equals("bytes9")) {
                result.add(new Bytes9(value.getBytes()));
            } else if (type.equals("bytes10")) {
                result.add(new Bytes10(value.getBytes()));
            } else if (type.equals("bytes11")) {
                result.add(new Bytes11(value.getBytes()));
            } else if (type.equals("bytes12")) {
                result.add(new Bytes12(value.getBytes()));
            } else if (type.equals("bytes13")) {
                result.add(new Bytes13(value.getBytes()));
            } else if (type.equals("bytes14")) {
                result.add(new Bytes14(value.getBytes()));
            } else if (type.equals("bytes15")) {
                result.add(new Bytes15(value.getBytes()));
            } else if (type.equals("bytes16")) {
                result.add(new Bytes16(value.getBytes()));
            } else if (type.equals("bytes17")) {
                result.add(new Bytes17(value.getBytes()));
            } else if (type.equals("bytes18")) {
                result.add(new Bytes18(value.getBytes()));
            } else if (type.equals("bytes19")) {
                result.add(new Bytes19(value.getBytes()));
            } else if (type.equals("bytes20")) {
                result.add(new Bytes20(value.getBytes()));
            } else if (type.equals("bytes21")) {
                result.add(new Bytes21(value.getBytes()));
            } else if (type.equals("bytes22")) {
                result.add(new Bytes22(value.getBytes()));
            } else if (type.equals("bytes23")) {
                result.add(new Bytes23(value.getBytes()));
            } else if (type.equals("bytes24")) {
                result.add(new Bytes24(value.getBytes()));
            } else if (type.equals("bytes25")) {
                result.add(new Bytes25(value.getBytes()));
            } else if (type.equals("bytes26")) {
                result.add(new Bytes26(value.getBytes()));
            } else if (type.equals("bytes27")) {
                result.add(new Bytes27(value.getBytes()));
            } else if (type.equals("bytes28")) {
                result.add(new Bytes28(value.getBytes()));
            } else if (type.equals("bytes29")) {
                result.add(new Bytes29(value.getBytes()));
            } else if (type.equals("bytes30")) {
                result.add(new Bytes30(value.getBytes()));
            } else if (type.equals("bytes31")) {
                result.add(new Bytes31(value.getBytes()));
            } else if (type.equals("bytes32")) {
                result.add(new Bytes32(value.getBytes()));
            } else {
                throw new UnsupportedOperationException("Unsupported type encountered: " + type);
            }
        }
        return result;
    }

    public static List<Type> getInputs(String jsonStr) throws Exception {
        List result = new ArrayList();
        if (TextUtils.isEmpty(jsonStr) || jsonStr.equals("[]") || jsonStr.equals("[{}]") || jsonStr.equals("[ ]")) {
            return result;
        }
        JSONArray jsonArray = new JSONArray(jsonStr);
        if (jsonArray == null || jsonArray.length() <= 0) {
            return result;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject typeValue = jsonArray.getJSONObject(i);
            Iterator<String> keys = typeValue.keys();
            while (keys.hasNext()) {
                String type = keys.next();
                String value = typeValue.optString(type);
                if (type.equals("address")) {
                    result.add(new Address(value));
                } else if (type.equals("bool")) {
                    result.add(new Bool(Boolean.parseBoolean(value)));
                } else if (type.equals("string")) {
                    result.add(new Utf8String(value));
                } else if (type.equals("bytes")) {
                    result.add(new DynamicBytes(value.getBytes()));
                } else if (type.equals("uint")) {
                    result.add(new Uint256(new BigInteger(value)));
                } else if (type.equals("int")) {
                    result.add(new Int256(new BigInteger(value)));
                } else if (type.equals("uint8")) {
                    result.add(new Uint8(new BigInteger(value)));
                } else if (type.equals("int8")) {
                    result.add(new Int8(new BigInteger(value)));
                } else if (type.equals("uint16")) {
                    result.add(new Uint16(new BigInteger(value)));
                } else if (type.equals("int16")) {
                    result.add(new Int16(new BigInteger(value)));
                } else if (type.equals("uint24")) {
                    result.add(new Uint24(new BigInteger(value)));
                } else if (type.equals("int24")) {
                    result.add(new Int24(new BigInteger(value)));
                } else if (type.equals("uint32")) {
                    result.add(new Uint32(new BigInteger(value)));
                } else if (type.equals("int32")) {
                    result.add(new Int32(new BigInteger(value)));
                } else if (type.equals("uint40")) {
                    result.add(new Uint40(new BigInteger(value)));
                } else if (type.equals("int40")) {
                    result.add(new Int40(new BigInteger(value)));
                } else if (type.equals("uint48")) {
                    result.add(new Uint48(new BigInteger(value)));
                } else if (type.equals("int48")) {
                    result.add(new Int48(new BigInteger(value)));
                } else if (type.equals("uint56")) {
                    result.add(new Uint56(new BigInteger(value)));
                } else if (type.equals("int56")) {
                    result.add(new Int56(new BigInteger(value)));
                } else if (type.equals("uint64")) {
                    result.add(new Uint64(new BigInteger(value)));
                } else if (type.equals("int64")) {
                    result.add(new Int64(new BigInteger(value)));
                } else if (type.equals("uint72")) {
                    result.add(new Uint72(new BigInteger(value)));
                } else if (type.equals("int72")) {
                    result.add(new Int72(new BigInteger(value)));
                } else if (type.equals("uint80")) {
                    result.add(new Uint80(new BigInteger(value)));
                } else if (type.equals("int80")) {
                    result.add(new Int80(new BigInteger(value)));
                } else if (type.equals("uint88")) {
                    result.add(new Uint88(new BigInteger(value)));
                } else if (type.equals("int88")) {
                    result.add(new Int88(new BigInteger(value)));
                } else if (type.equals("uint96")) {
                    result.add(new Uint96(new BigInteger(value)));
                } else if (type.equals("int96")) {
                    result.add(new Int96(new BigInteger(value)));
                } else if (type.equals("uint104")) {
                    result.add(new Uint104(new BigInteger(value)));
                } else if (type.equals("int104")) {
                    result.add(new Int104(new BigInteger(value)));
                } else if (type.equals("uint112")) {
                    result.add(new Uint112(new BigInteger(value)));
                } else if (type.equals("int112")) {
                    result.add(new Int112(new BigInteger(value)));
                } else if (type.equals("uint120")) {
                    result.add(new Uint120(new BigInteger(value)));
                } else if (type.equals("int120")) {
                    result.add(new Int120(new BigInteger(value)));
                } else if (type.equals("uint128")) {
                    result.add(new Uint128(new BigInteger(value)));
                } else if (type.equals("int128")) {
                    result.add(new Int128(new BigInteger(value)));
                } else if (type.equals("uint136")) {
                    result.add(new Uint136(new BigInteger(value)));
                } else if (type.equals("int136")) {
                    result.add(new Int136(new BigInteger(value)));
                } else if (type.equals("uint144")) {
                    result.add(new Uint144(new BigInteger(value)));
                } else if (type.equals("int144")) {
                    result.add(new Int144(new BigInteger(value)));
                } else if (type.equals("uint152")) {
                    result.add(new Uint152(new BigInteger(value)));
                } else if (type.equals("int152")) {
                    result.add(new Int152(new BigInteger(value)));
                } else if (type.equals("uint160")) {
                    result.add(new Uint160(new BigInteger(value)));
                } else if (type.equals("int160")) {
                    result.add(new Int160(new BigInteger(value)));
                } else if (type.equals("uint168")) {
                    result.add(new Uint168(new BigInteger(value)));
                } else if (type.equals("int168")) {
                    result.add(new Int168(new BigInteger(value)));
                } else if (type.equals("uint176")) {
                    result.add(new Uint176(new BigInteger(value)));
                } else if (type.equals("int176")) {
                    result.add(new Int176(new BigInteger(value)));
                } else if (type.equals("uint184")) {
                    result.add(new Uint184(new BigInteger(value)));
                } else if (type.equals("int184")) {
                    result.add(new Int184(new BigInteger(value)));
                } else if (type.equals("uint192")) {
                    result.add(new Uint192(new BigInteger(value)));
                } else if (type.equals("int192")) {
                    result.add(new Int192(new BigInteger(value)));
                } else if (type.equals("uint200")) {
                    result.add(new Uint200(new BigInteger(value)));
                } else if (type.equals("int200")) {
                    result.add(new Int200(new BigInteger(value)));
                } else if (type.equals("uint208")) {
                    result.add(new Uint208(new BigInteger(value)));
                } else if (type.equals("int208")) {
                    result.add(new Int208(new BigInteger(value)));
                } else if (type.equals("uint216")) {
                    result.add(new Uint216(new BigInteger(value)));
                } else if (type.equals("int216")) {
                    result.add(new Int216(new BigInteger(value)));
                } else if (type.equals("uint224")) {
                    result.add(new Uint224(new BigInteger(value)));
                } else if (type.equals("int224")) {
                    result.add(new Int224(new BigInteger(value)));
                } else if (type.equals("uint232")) {
                    result.add(new Uint232(new BigInteger(value)));
                } else if (type.equals("int232")) {
                    result.add(new Int232(new BigInteger(value)));
                } else if (type.equals("uint240")) {
                    result.add(new Uint240(new BigInteger(value)));
                } else if (type.equals("int240")) {
                    result.add(new Int240(new BigInteger(value)));
                } else if (type.equals("uint248")) {
                    result.add(new Uint248(new BigInteger(value)));
                } else if (type.equals("int248")) {
                    result.add(new Int248(new BigInteger(value)));
                } else if (type.equals("uint256")) {
                    result.add(new Uint256(new BigInteger(value)));
                } else if (type.equals("int256")) {
                    result.add(new Int256(new BigInteger(value)));
                } else if (type.equals("bytes1")) {
                    result.add(new Bytes1(value.getBytes()));
                } else if (type.equals("bytes2")) {
                    result.add(new Bytes2(value.getBytes()));
                } else if (type.equals("bytes3")) {
                    result.add(new Bytes3(value.getBytes()));
                } else if (type.equals("bytes4")) {
                    result.add(new Bytes4(value.getBytes()));
                } else if (type.equals("bytes5")) {
                    result.add(new Bytes5(value.getBytes()));
                } else if (type.equals("bytes6")) {
                    result.add(new Bytes6(value.getBytes()));
                } else if (type.equals("bytes7")) {
                    result.add(new Bytes7(value.getBytes()));
                } else if (type.equals("bytes8")) {
                    result.add(new Bytes8(value.getBytes()));
                } else if (type.equals("bytes9")) {
                    result.add(new Bytes9(value.getBytes()));
                } else if (type.equals("bytes10")) {
                    result.add(new Bytes10(value.getBytes()));
                } else if (type.equals("bytes11")) {
                    result.add(new Bytes11(value.getBytes()));
                } else if (type.equals("bytes12")) {
                    result.add(new Bytes12(value.getBytes()));
                } else if (type.equals("bytes13")) {
                    result.add(new Bytes13(value.getBytes()));
                } else if (type.equals("bytes14")) {
                    result.add(new Bytes14(value.getBytes()));
                } else if (type.equals("bytes15")) {
                    result.add(new Bytes15(value.getBytes()));
                } else if (type.equals("bytes16")) {
                    result.add(new Bytes16(value.getBytes()));
                } else if (type.equals("bytes17")) {
                    result.add(new Bytes17(value.getBytes()));
                } else if (type.equals("bytes18")) {
                    result.add(new Bytes18(value.getBytes()));
                } else if (type.equals("bytes19")) {
                    result.add(new Bytes19(value.getBytes()));
                } else if (type.equals("bytes20")) {
                    result.add(new Bytes20(value.getBytes()));
                } else if (type.equals("bytes21")) {
                    result.add(new Bytes21(value.getBytes()));
                } else if (type.equals("bytes22")) {
                    result.add(new Bytes22(value.getBytes()));
                } else if (type.equals("bytes23")) {
                    result.add(new Bytes23(value.getBytes()));
                } else if (type.equals("bytes24")) {
                    result.add(new Bytes24(value.getBytes()));
                } else if (type.equals("bytes25")) {
                    result.add(new Bytes25(value.getBytes()));
                } else if (type.equals("bytes26")) {
                    result.add(new Bytes26(value.getBytes()));
                } else if (type.equals("bytes27")) {
                    result.add(new Bytes27(value.getBytes()));
                } else if (type.equals("bytes28")) {
                    result.add(new Bytes28(value.getBytes()));
                } else if (type.equals("bytes29")) {
                    result.add(new Bytes29(value.getBytes()));
                } else if (type.equals("bytes30")) {
                    result.add(new Bytes30(value.getBytes()));
                } else if (type.equals("bytes31")) {
                    result.add(new Bytes31(value.getBytes()));
                } else if (type.equals("bytes32")) {
                    result.add(new Bytes32(value.getBytes()));
                } else {
                    throw new UnsupportedOperationException("Unsupported type encountered: " + type);
                }
            }
        }

        return result;
    }
}
