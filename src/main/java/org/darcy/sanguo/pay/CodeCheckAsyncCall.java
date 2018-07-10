package org.darcy.sanguo.pay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.player.Player;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class CodeCheckAsyncCall extends AsyncCall {
	String code;
	int dropId = -1;
	String errorInfo;
	boolean result = false;
	Player player;
	int type;

	public CodeCheckAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
		this.player = session.getPlayer();
		try {
			this.code = PbUp.CodeCheck.parseFrom(packet.getData()).getCode();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public void callback() {
		PbDown.CodeCheckRst.Builder rst = PbDown.CodeCheckRst.newBuilder();
		rst.setResult(false);
		if (this.result) {
			Set records = this.player.getPool().getIntegers(20);
			if ((!(records.contains(Integer.valueOf(this.type)))) || (this.type == -1)) {
				DropGroup dg = (DropGroup) DropService.dropGroups.get(Integer.valueOf(this.dropId));
				List<Gain> gains = dg.genGains(this.player);
				List list = new ArrayList();
				for (Gain gain : gains) {
					gain.gain(this.player, "rewardcode");
					list.add(gain.newReward());
				}
				records.add(Integer.valueOf(this.type));
				this.player.getPool().set(20, records);
				this.player.notifyGetItem(2, list);
				rst.setResult(true);
			} else {
				rst.setErrInfo("已经兑换过该类礼包");
			}
		} else {
			rst.setErrInfo(this.errorInfo);
		}

		this.player.send(2154, rst.build());
	}

	public void netOrDB() {
		if ((this.code != null) && (this.code.trim().length() > 0)) {
			this.code = this.code.trim();
			try {
				this.code = URLEncoder.encode(this.code, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			try {
				URL url = new URL(Configuration.billingAdd + "/keycheck?cdkey=" + this.code);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				http.setConnectTimeout(5000);
				http.setReadTimeout(5000);
				http.setRequestMethod("GET");
				http.setDoInput(true);
				http.setDoOutput(true);

				Reader reader = new InputStreamReader(http.getInputStream(), "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(reader);
				String str = null;
				StringBuffer sb = new StringBuffer();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str);
				}

				JSONObject json = JSONObject.fromObject(sb.toString());
				KeyCheckRst rst = (KeyCheckRst) JSONObject.toBean(json, KeyCheckRst.class);

				this.dropId = rst.getDrop();
				this.result = rst.getResult();
				this.type = rst.getType();
				if (rst.getErrorCode() != 0)
					if (rst.getErrorCode() == 2)
						this.errorInfo = "无效的激活码";
					else if (rst.getErrorCode() == 1)
						this.errorInfo = "该激活码已经使用或者已达激活次数上限";
					else if (rst.getErrorCode() == 4)
						this.errorInfo = "激活码已经过有效期";
					else if (rst.getErrorCode() == 3)
						this.errorInfo = "激活码格式不正确";
					else if (rst.getErrorCode() == 6)
						this.errorInfo = "激活码还未生效";
					else
						this.errorInfo = "服务器繁忙，请稍后再试";
			} catch (Exception e) {
				e.printStackTrace();
				this.errorInfo = "服务器繁忙，请稍后再试";
			}
		}
	}
}
