/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 K.Misaki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mickey305.common.v2.json.model;

import com.mickey305.foundation.v3.compat.stream.Supplier;
import com.mickey305.foundation.v3.util.pattern.Component;
import com.mickey305.foundation.v3.util.pattern.Composite;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TypeNode<T extends TypeTree> extends Composite<T> {
    private Collection<Component<T>> parents;

    public <E extends Collection<Component<T>>> TypeNode(T object, Supplier<E> bindDataStructure) {
        super(object, bindDataStructure);
        this.parents = new HashSet<>();
    }

    public TypeNode(T object) {
        this(object, HashSet::new);
    }

    @Override public boolean add(Component<T> child) {
        return child instanceof TypeNode && this.add((TypeNode<T>) child);
    }

    public boolean add(TypeNode<T> child) {
        if (child.unbox() instanceof Type) {
            // child element: Type enum situation
            child.getParents().add(this);
            return super.getChildren().add(child);
        }
        return super.add(child);
    }

    public Collection<Component<T>> getParents() {
        return (this.unbox() instanceof Type)
                ? this.parents
                : Collections.singletonList(this.getParent());
    }

    @Override public boolean belongsTo(Component<T> target) {
        return target instanceof TypeNode && this.belongsTo((TypeNode<T>) target);
    }

    public boolean belongsTo(TypeNode<T> target) {
        Collection<Component<T>> parents = new HashSet<>(this.getParents());
        if (this.unbox() instanceof Type) {
            return parents.stream().anyMatch(parent ->
                    parent.getCallback().search(parent, target) || parent.belongsTo(target));
        }
        return super.belongsTo(target);
    }

    public T unbox() {
        return this.getObject();
    }

    // only Readable and available Element-binding, Composite-Pattern
    @Deprecated @Override public boolean addParent(Composite<T> composite) { return false; }
    @Deprecated @Override public boolean remove(Component<T> component) { return false; }
    @Deprecated @Override public boolean removeParent() { return false; }
    @Deprecated @Override public boolean removeAll(Collection<Component<T>> components) { return false; }
    @Deprecated @Override public boolean removeAll() { return false; }

    public boolean is(Group group) {
        return this.unbox() instanceof Group && ((Group) this.unbox()).is(group);
    }

    public boolean is(Type type) {
        return this.unbox() instanceof Type && ((Type) this.unbox()).is(type);
    }
}
