package lr;

import java.util.*;

/**
 * Created by luca on 24/02/16.
 */

public class ConsistentHash<T> {

    private TreeMap<Integer, T> _map;
    private int _replication;
    private final static int default_replication = 3;

    public ConsistentHash() {
        this(default_replication);
    }

    public ConsistentHash(int replication) {
        _replication = replication;
        _map = new TreeMap<>();
    }

    public ConsistentHash(Collection<T> list, int replication) {
        this(replication);
        for (T item : list) {
            add(item);
        }
    }

    public ConsistentHash(Collection<T> list) {
        this(list, default_replication);
    }

    synchronized public boolean add(T node) {
        boolean insert = true;
        for (int i = 0; i < _replication; i++) {
            int hash = Helper.hash(node.toString() + i);
            if (!_map.containsKey(hash))
                _map.put(hash, node);
            else
                insert = false;
        }
        return insert;
    }

//    public boolean add(String key, T node) {
//        if (!_map.containsKey(MurmurHash.hash32(key))) {
//            _map.put(MurmurHash.hash32(key), node);
//            return true;
//        } else
//            return false;
//    }

    synchronized public TreeMap<Integer,T> getMap() {
    //    System.out.println("MAP " + _map);
        return _map;
    }

    synchronized public void remove(T node) {
        for (int i = 0; i < _replication; i++)
            _map.remove(Helper.hash(node.toString() + i));
    }

    synchronized public T get(int key) {
        System.out.println("MAP "+_map);
        Integer res = _map.ceilingKey(key);
        if (res != null) {
            T n = _map.get(res);
            System.out.println("consistent hashtable map "+ key +" to node "+n);
            return n;
        } else {
            return _map.firstEntry().getValue();
        }
    }

    synchronized public List<T> getNext(String key, int n) {
        List<T> list = new ArrayList<>();
        List<Integer> hashs = getHashesForKey(key);
        int hash = hashs.get(0);
        int i=0;
        while (i<n) {
            Map.Entry<Integer, T> entry = _map.ceilingEntry(hash);
            if (entry == null) entry = _map.firstEntry();
            if (!hashs.contains(entry.getKey()) && !list.contains(entry.getValue())) {
                list.add(entry.getValue());
                i++;
            }
            hash = entry.getKey()+1;
        }
        System.out.println("getNext "+list);
        return list;
    }

    public List<Integer> getHashesForKey (String key){
        List<Integer> hashes = new ArrayList<>();
        for (int i=0; i<_replication; i++){
            hashes.add(Helper.hash(key + i));
        }
        return hashes;
    }

    synchronized public List<T> getPrev(String key, int n) {
        List<T> list = new ArrayList<>();
        List<Integer> hashes = getHashesForKey(key);
        int hash = hashes.get(0);
        int i=0;
        while (i < n) {
            Map.Entry<Integer, T> entry = _map.floorEntry(hash);
            if (entry == null) entry = _map.lastEntry();
            if (!hashes.contains(entry.getKey()) && !list.contains(entry.getValue())) {
                list.add(entry.getValue());
                i++;
            }
            hash = entry.getKey()-1;
        }
        System.out.println("getPrev "+list);
        return list;
    }

    @Override
    public String toString() {
        String res = "";
        for (int key : _map.descendingKeySet()) {
            res += "\tkey: " + key + " value: " + _map.get(key) + "\n\t";
        }
        return res;
    }

    synchronized public int size() {
        return _map.size();
    }
}
