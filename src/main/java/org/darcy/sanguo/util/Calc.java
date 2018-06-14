package org.darcy.sanguo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.util.HexBin;

public class Calc {
	private static Random random = new Random(System.currentTimeMillis());

	private static AtomicInteger tempId = new AtomicInteger(10000);

	public static int getTempId() {
		return tempId.incrementAndGet();
	}

	public static Integer[] randomGet(int length, int size) {
		if (length < size) {
			throw new IllegalArgumentException();
		}
		HashSet rst = new HashSet();
		if (length == size) {
			for (int i = 0; i < length; ++i)
				rst.add(Integer.valueOf(i));
		} else {
			do
				rst.add(Integer.valueOf(nextInt(length)));
			while (rst.size() < size);
		}

		Integer[] r = new Integer[size];
		rst.toArray(r);
		return r;
	}

	public static int nextInt(int bound) {
		return random.nextInt(bound);
	}

	public static int count1(int v) {
		int number = 0;
		while (v > 0) {
			if (v % 2 == 1) {
				++number;
			}
			v >>= 1;
		}
		return number;
	}

	public static int randomGet(int[] array, int except) {
		int j;
		int count = 0;
		for (int i : array) {
			if (i == except)
				continue;
			++count;
		}

		if (count < 1)
			return -1;

		int r = nextInt(count);
		for (j = 0; j < array.length; ++j) {
			if (array[j] != except) {
				if (r == 0)
					return j;
				--r;
			}
		}

		return -1;
	}

	public static int randomByWeights(int[] weights) {
		int j;
		int l;
		int len = weights.length;
		int sum = 0;
		for (int n : weights) {
			sum += n;
		}
		if (sum <= 0) {
			return -1;
		}
		int rand = nextInt(sum);
		j = 0;
		l = 0;
		do {
			j += weights[l];
			if (rand < j)
				return l;
			++l;
		} while (l < len);
		return (len - 1);
	}

	public static int randomFromArray(int[] weights) {
		int j;
		int l;
		int len = weights.length;
		int sum = 0;
		for (int n : weights) {
			sum += n;
		}
		if (sum <= 0) {
			return 0;
		}
		int rand = nextInt(sum);
		j = 0;
		l = 0;
		do {
			j += weights[l];
			if (rand < j)
				return l;
			++l;
		} while (l < len);
		return (len - 1);
	}

	public static int weight(int[] weight) {
		int j;
		int l;
		if (weight.length < 1) {
			throw new IllegalArgumentException();
		}

		int sum = 0;
		for (int i : weight) {
			if (i < 0)
				throw new IllegalArgumentException();
			sum += i;
		}

		int r = nextInt(sum);
		j = 0;
		for (l = 0; l < weight.length; ++l) {
			j += weight[l];
			if (r < j) {
				return l;
			}
		}
		return -1;
	}

	public static int weight(int ran, int[] weight) {
		int j;
		int l;
		if (weight.length < 1) {
			throw new IllegalArgumentException();
		}

		int sum = 0;
		for (int i : weight) {
			if (i < 0)
				throw new IllegalArgumentException();
			sum += i;
		}

		int r = ran % sum;

		j = 0;
		for (l = 0; l < weight.length; ++l) {
			j += weight[l];
			if (r < j) {
				return l;
			}
		}
		return -1;
	}

	public static String md5(String sourceStr) {
		try {
			byte[] bytes = sourceStr.getBytes("utf-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] md5Byte = md5.digest();
			if (md5Byte != null)
				return HexBin.encode(md5Byte).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if ((src == null) || (src.length <= 0)) {
			return null;
		}
		for (int i = 0; i < src.length; ++i) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if ((hexString == null) || (hexString.equals(""))) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; ++i) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static int[] split(String s, String flag) {
		int[] rst = null;
		try {
			String[] ls = s.split(flag);
			rst = new int[ls.length];
			for (int i = 0; i < rst.length; ++i) {
				String l = ls[i];
				rst[i] = Integer.parseInt(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(s);
		}

		return rst;
	}

	public static int versionCompare(String v1, String v2) {
		int[] iv1 = split(v1, "\\.");
		int[] iv2 = split(v2, "\\.");
		for (int i = 0; i < iv1.length; ++i) {
			if (iv1[i] < iv2[i])
				return -1;
			if (iv1[i] > iv2[i]) {
				return 1;
			}
		}

		return 0;
	}

	public static String box(int value, int length) {
		String rst = String.valueOf(value);
		StringBuffer sb = new StringBuffer();
		while (sb.length() + rst.length() < length) {
			sb.append('0');
		}
		sb.append(rst);
		return sb.toString();
	}
}
