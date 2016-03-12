import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
import lr.core.*;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by luca on 12/03/16.
 */
public class ModServer {
    @Test
    public void addServer() throws UnknownHostException, InterruptedException {
        final List<NodeService> clients = new ArrayList<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 2;
        int clusterMembers = 2;

        for (int i = 1; i < seedNodes + 1; ++i) {
            startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
        }

        for (int i = 1; i < clusterMembers + 1; ++i) {
            clients.add(new NodeService(i + "", "127.0.0." + i, 2000, startupMembers)
                    .clearStorage()
                    .setNBackup(0)
                    .start());
        }

        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

        Thread.sleep(5000);

        System.out.print("key: " + Helper.MD5ToLong("hkjasdjkhdsahjkasdsad"));
        r.getRandomNode().send(new MessageRequest<>(r, Message.MSG_OPERATION.ADD, "hkjasdjkhdsahjkasdsad", "PROVA"));
        r.<MessageResponse<?>>receive().ifPresent(message -> {
            Assert.assertEquals(MessageResponse.MSG_STATUS.OK, message.getStatus());
        });

        try {
            Thread.sleep(2000);
            clients.add(new NodeService("3", "127.0.0.3", 2000, startupMembers)
                    .clearStorage()
                    .setNBackup(0)
                    .start());

            Thread.sleep(5000);

            r.getRandomNode().send(new MessageRequest<>(r, Message.MSG_OPERATION.DEL, "hkjasdjkhdsahjkasdsad"));
            r.<MessageResponse<?>>receive().ifPresent(message -> {
                Assert.assertEquals(MessageResponse.MSG_STATUS.OK, message.getStatus());
            });


            NodeService n = clients.get(2);
            try {
                Field storeField = NodeService.class.getDeclaredField("_store");
                storeField.setAccessible(true);
                PersistentStorage store = (PersistentStorage) storeField.get(n);

                System.out.print("\t STORE of " + n.getId() + ": ");
                for (Map.Entry<String, Data<?>> i : store.getMap().entrySet()) {
                    System.out.print(i.getValue() + ", ");
                }
                Assert.assertEquals(0, store.getMap().size());
                System.out.println();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


        } catch (UnknownHostException |
                InterruptedException e
                )

        {
            e.printStackTrace();
        }


//        while (true) {
//            System.out.print("\n...");
//            Thread.sleep(2000);
//            System.out.println();
//            for (NodeService n : clients) {
//                try {
//                    Field privateField = NodeService.class.getDeclaredField("_ch");
//                    privateField.setAccessible(true);
//                    ConsistentHash<Node> ch = (ConsistentHash<Node>) privateField.get(n);
//
//                    Map<Long, Node> map = ch.getMap();
//                    System.out.print("CH of " + n.getId() + "(" + ch.getHashesForKey(n.toString()) + "): ");
//                    for (Map.Entry<Long, Node> i : map.entrySet()) {
//                        System.out.print(i.getValue().getId() + ", ");
//                    }
//
//                    Field storeField = NodeService.class.getDeclaredField("_store");
//                    storeField.setAccessible(true);
//                    PersistentStorage store = (PersistentStorage) storeField.get(n);
//
//                    System.out.print("\t STORE of " + n.getId() + ": ");
//                    for (Map.Entry<String, Data<?>> i : store.getMap().entrySet()) {
//                        System.out.print(i.getValue() + ", ");
//                    }
//                    System.out.println();
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}