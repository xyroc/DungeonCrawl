package xiroc.dungeoncrawl.util.collections;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An AVL-Tree implementation. There is no removal method since it is not needed.
 */
public class AVLTree<T> {
    private Node<T> root;

    public boolean isEmpty() {
        return root == null;
    }

    public void traverseRecursively(BiConsumer<T, Integer> consumer) {
        if (root == null) return;
        traverseRecursively(consumer, root);
    }

    /*
     * Only used in data gen, so no need for this to be particularly fast.
     */
    private void traverseRecursively(BiConsumer<T, Integer> consumer, Node<T> node) {
        consumer.accept(node.value, node.key);
        if (node.right != null) {
            traverseRecursively(consumer, node.right);
        }
        if (node.left != null) {
            traverseRecursively(consumer, node.left);
        }
    }

    public void insert(T value, int key) {
        if (root == null) {
            root = new Node<>(null, value, key);
            return;
        }
        Node<T> node = root;
        while (true) {
            if (key > node.key) {
                if (node.right == null) {
                    node.right = new Node<>(node, value, key);
                    rebalance(node);
                    return;
                } else {
                    node = node.right;
                }
            } else if (key < node.key) {
                if (node.left == null) {
                    node.left = new Node<>(node, value, key);
                    rebalance(node);
                    return;
                } else {
                    node = node.left;
                }
            } else {
                node.value = value;
                return;
            }
        }
    }

    /**
     * Searches for the value with the lowest key strictly greater than the given one
     */
    public T findSupremum(int key) {
        if (root == null) {
            return null;
        }
        Node<T> node = root;
        T bestMatch = null;
        while (true) {
            if (node.key > key) {
                bestMatch = node.value;
                if (node.left != null) {
                    node = node.left;
                } else {
                    return bestMatch;
                }
            } else {
                if (node.right != null) {
                    node = node.right;
                } else {
                    return bestMatch;
                }
            }
        }
    }

    private void rebalance(Node<T> node) {
        Node<T> previousNode = null;
        while (true) {
            node.updateHeight();
            int heightDifference = node.heightDifference();
            switch (heightDifference) {
                case -1, 1:
                    if (node.parent == null) {
                        return;
                    }
                    previousNode = node;
                    node = node.parent;
                    continue;
                case -2, 2:
                    Objects.requireNonNull(previousNode);
                    switch (heightDifference + previousNode.heightDifference()) {
                        case -3 -> node.rotateRight();
                        case 3 -> node.rotateLeft();
                        case -1 -> {
                            previousNode.rotateRight();
                            node.rotateLeft();
                        }
                        case 1 -> {
                            previousNode.rotateLeft();
                            node.rotateRight();
                        }
                        default -> throw new IllegalStateException("Invalid height differences: Parent: " + heightDifference + ", Child: " + previousNode.heightDifference());
                    }
                    findRoot();
                    return;
                default:
                    return;
            }
        }
    }

    private void findRoot() {
        Node<T> node = root;
        while (node.parent != null) {
            node = node.parent;
        }
        root = node;
    }


    private static class Node<T> {
        private final int key;
        private T value;

        private Node<T> parent;
        private Node<T> left;
        private Node<T> right;

        private int height;

        public Node(Node<T> parent, T value, int key) {
            this.parent = parent;
            this.value = value;
            this.key = key;
            this.height = 1;
        }

        private int heightDifference() {
            return height(right) - height(left);
        }

        private void rotateLeft() {
            Node<T> rightTmp = right;
            right = right.left;
            rightTmp.parent = parent;
            rightTmp.left = this;
            parent = rightTmp;
            updateHeight();
            rightTmp.updateHeight();
            if (rightTmp.parent != null) {
                if (this == rightTmp.parent.left) {
                    rightTmp.parent.left = parent;
                } else {
                    rightTmp.parent.right = parent;
                }
                rightTmp.parent.updateHeight();
            }
        }

        private void rotateRight() {
            Node<T> leftTmp = left;
            left = left.right;
            leftTmp.parent = parent;
            leftTmp.right = this;
            parent = leftTmp;
            updateHeight();
            leftTmp.updateHeight();
            if (leftTmp.parent != null) {
                if (this == leftTmp.parent.left) {
                    leftTmp.parent.left = parent;
                } else {
                    leftTmp.parent.right = parent;
                }
                leftTmp.parent.updateHeight();
            }
        }

        private void updateHeight() {
            height = 1 + Math.max(height(left), height(right));
        }

        private static int height(Node<?> node) {
            return node == null ? 0 : node.height;
        }
    }
}