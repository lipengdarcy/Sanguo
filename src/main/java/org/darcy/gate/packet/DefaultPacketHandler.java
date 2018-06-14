package org.darcy.gate.packet;

import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.darcy.gate.net.ClientSession;
import org.darcy.gate.server.GateServer;
import org.darcy.gate.server.ServerManager;
import org.darcy.gate.version.VersionManager;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbGate;
import sango.packet.PbPacket;

public class DefaultPacketHandler {
	public static void handlePacket(ClientSession session, PbPacket.Packet packet) {
		switch (packet.getPtCode()) {
		case 101:
			regist(session, packet);
			break;
		case 103:
			break;
		case 105:
			version(session);
			break;
		case 102:
		case 104:
		default:
			LogFactory.getLog(DefaultPacketHandler.class).error("unknown ptCode: " + packet.getPtCode());
		}
	}

	public static void version(ClientSession session) {
		PbGate.GateVersionRst.Builder rst = PbGate.GateVersionRst.newBuilder()
				.setVersion(VersionManager.getLatestVersion());
		session.send(106, rst.build());
	}

	public static void heart(ClientSession session) {
		PbGate.GateHeartRst rst = PbGate.GateHeartRst.newBuilder().build();
		session.send(104, rst);
	}

	public static void regist(ClientSession session, PbPacket.Packet packet) {
		PbGate.GateRegisterRst rst = null;
		try {
			PbGate.GateRegister register = PbGate.GateRegister.parseFrom(packet.getData());
			for (Iterator<?> localIterator = register.getNumbersList().iterator(); localIterator.hasNext();) {
				int number = ((Integer) localIterator.next()).intValue();
				GateServer server = ServerManager.getInstance().getServerByNumber(number);
				if (server == null) {
					server = new GateServer();
					server.setId(register.getId());
					server.setIp(register.getIp());
					server.setName(register.getName());
					server.setPort(register.getPort());
					server.setSession(session);
					server.setStatus(1);
					server.setTest(register.getTest());
					server.setNumber(number);
					ServerManager.getInstance().addServer(server);
				} else {
					if ((server.getSession() != null) && (!(server.getSession().isDisconnect()))) {
						server.getSession().disconnect();
					}
					server.setIp(register.getIp());
					server.setPort(register.getPort());
					server.setSession(session);
					server.setTest(register.getTest());
				}
			}

			rst = PbGate.GateRegisterRst.newBuilder().setResult(true).build();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			rst = PbGate.GateRegisterRst.newBuilder().setResult(false).build();
		}

		session.send(102, rst);
	}
}
