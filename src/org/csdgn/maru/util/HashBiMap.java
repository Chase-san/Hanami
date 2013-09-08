package org.csdgn.maru.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashBiMap<K, V> implements Map<K, V> {
	private HashMap<K,V> keys;
	private HashMap<V,K> vals;
	private HashBiMap<V,K> inverse;
	
	public HashBiMap() {
		initalize();
	}
	
	public HashBiMap(K[] keys, V[] vals) {
		initalize();
		for(int i = 0; i < keys.length; ++i) {
			put(keys[i],vals[i]);
		}
	}
	
	private HashBiMap(boolean doNotInit) {
		if(!doNotInit)
			initalize();
	}
	
	private void initalize() {
		keys = new HashMap<K,V>();
		vals = new HashMap<V,K>();
		inverse = new HashBiMap<V,K>(true);
		inverse.keys = vals;
		inverse.vals = keys;
		inverse.inverse = this;
	}
	
	public HashBiMap<V, K> inverse() {
		return inverse;
	}

	@Override
	public void clear() {
		keys.clear();
		vals.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return keys.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return vals.containsKey(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return keys.entrySet();
	}

	@Override
	public V get(Object key) {
		return keys.get(key);
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return keys.keySet();
	}

	@Override
	public V put(K key, V value) {
		vals.put(value, key);
		return keys.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(K key : m.keySet()) {
			put(key,m.get(key));
		}
	}

	@Override
	public V remove(Object key) {
		V val = keys.remove(key);
		vals.remove(val);
		return val;
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public Collection<V> values() {
		return keys.values();
	}
}
