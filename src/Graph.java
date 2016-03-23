import unalcol.types.collection.vector.Vector;

/**
 * Created by Eduardo Galeano on 13/03/16.
 */


public class Graph {

    public Vector<Node> nodes = new Vector<>();


    public Graph(){
    }

    /**
     * use this if you want to build a simple graph
     * @param coordinates
     */
    public Graph(int[] coordinates){
        this.nodes.add(new Node(coordinates));
    }

    public void add_new_node(int[] coordinates){
        this.nodes.add(new Node(coordinates));
    }

    public void add_front_child(int[] node, int[] neighbor){
        Node[] nodes = find_node_pair(node, neighbor);

        if (nodes[1] != null){
            nodes[0].front_child = nodes[1];
            nodes[1].back_child = nodes[0];
        }

        else{
            Node front_child = new Node(neighbor);
            nodes[0].front_child = front_child;
            front_child.back_child = nodes[0];
            this.nodes.add(front_child);
        }
    }


    public void add_back_child(int[] node, int[] neighbor){
        Node[] nodes = find_node_pair(node, neighbor);

        if (nodes[1] != null){
            nodes[0].back_child = nodes[1];
            nodes[1].front_child = nodes[0];
        }

        else{
            Node back_child = new Node(neighbor);
            nodes[0].back_child = back_child;
            back_child.front_child = nodes[0];
            this.nodes.add(back_child);
        }
    }


    public void add_left_child(int[] node, int[] neighbor){
        Node[] nodes = find_node_pair(node, neighbor);

        if (nodes[1] != null){
            nodes[0].left_child = nodes[1];
            nodes[1].right_child = nodes[0];
        }

        else{
            Node left_child = new Node(neighbor);
            nodes[0].left_child = left_child;
            left_child.right_child = nodes[0];
            this.nodes.add(left_child);
        }
    }


    public void add_right_child(int[] node, int[] neighbor){
        Node[] nodes = find_node_pair(node, neighbor);

        if (nodes[1] != null){
            nodes[0].right_child = nodes[1];
            nodes[1].left_child = nodes[0];
        }

        else{
            Node right_child = new Node(neighbor);
            nodes[0].right_child = right_child;
            right_child.left_child = nodes[0];
            this.nodes.add(right_child);
        }
    }


    public int[] get_back_neighbor(int[] node){
        Node found = get_node(this.nodes, node);
        return found.back_child.coordinates;
    }


    public int[] get_front_neighbor(int[] node){
        Node found = get_node(this.nodes, node);
        return found.front_child.coordinates;
    }


    public int[] get_right_neighbor(int[] node){
        Node found = get_node(this.nodes, node);
        return found.right_child.coordinates;
    }


    public int[] get_left_neighbor(int[] node){
        Node found = get_node(this.nodes, node);
        return found.left_child.coordinates;
    }

    /**
     * looks for the two given nodes in the graph.
     * @param node1 the first node
     * @param node2 the second node
     * @return a Node array containing the nodes with the given coordinates, or null if otherwise
     */
    private Node[] find_node_pair(int[] node1, int[] node2) {
        boolean found = false;
        int counter = 0;
        Node[] nodes = new Node[]{null, null};

        while (counter < this.nodes.size() && !found) {
            Node actual_node = this.nodes.get(counter);

            if (actual_node.equals(node1))
                nodes[0] = actual_node;

            else if (actual_node.equals(node2))
                nodes[1] = actual_node;

            found = (nodes[0] != null && nodes[1] != null);
            counter++;
        }

        return nodes;
    }

    /**
     * returns the position of the first (and hopefully only) ocurrence of the node
     * with the given coordinates
     * @param coordinates the coordinates to look for
     * @return -1 if the node doesn't exist in the graph
     */
    public int find_node_position(Vector<Node> list, int[] coordinates){
        boolean found = false;
        int position = 0;

        while (position < list.size() && !found){
            Node node = list.get(position);
            found = (node.equals(coordinates));
            position++;
        }

        position = found? position : -1;

        return position;
    }


    private Node get_node(Vector<Node> list, int[] coordinates){
        boolean found = false;
        int position = 0;
        Node node = null;

        while (position < list.size() && !found){
            node = list.get(position);
            found = (node.equals(coordinates));
            position++;
        }

        return node;
    }


    /*
     * A* implementation
     */
    public Vector<int[]> get_path_to_objective(int[] start, int[] end){
        Vector<int[]> path = new Vector<>();
        Vector<Node> open_list = new Vector<>();
        Vector<Node> closed_list = new Vector<>();
        open_list.add(get_node(this.nodes, start));
        boolean found = open_list.get(0).equals(end);

        while (!found && !open_list.isEmpty()){
            int actual_index = find_min_f_cost_index(open_list);
            Node actual = open_list.get(actual_index);
            closed_list.add(actual);
            open_list.remove(actual_index);
            path.add(actual.coordinates);
            found = actual.equals(end);

            if (!found){
                a_star_logic(end, actual, actual.back_child, closed_list, open_list);
                a_star_logic(end, actual, actual.front_child, closed_list, open_list);
                a_star_logic(end, actual, actual.left_child, closed_list, open_list);
                a_star_logic(end, actual, actual.right_child, closed_list, open_list);
            }

        }

        return path;
    }


    private void a_star_logic(int[] end, Node actual, Node neighbor, Vector<Node> closed, Vector<Node> open){

        if (neighbor != null){

            if (!is_contained(closed, neighbor.coordinates)){
                Node maybe = get_node(open, neighbor.coordinates);
                int g_cost = actual.g_cost+1;
                int h_cost = calc_manhattan(neighbor.coordinates, end);
                int f_cost = g_cost + h_cost;

                if (maybe == null){
                    Node to_add = new Node(neighbor.coordinates);
                    to_add.g_cost = g_cost;
                    to_add.h_cost = h_cost;
                    to_add.f_cost = f_cost;
                    open.add(to_add);
                }

                else {

                    if (f_cost < maybe.f_cost){
                        maybe.g_cost = g_cost;
                        maybe.h_cost = h_cost;
                        maybe.f_cost = f_cost;
                    }
                }
            }
        }
    }


    private boolean is_contained(Vector<Node> list, int[] node){
        return get_node(this.nodes, node) != null;
    }


    private int calc_manhattan(int[] start, int[] end){
        int horizontals = Math.abs(start[0]-end[0]);
        int verticals = Math.abs(start[0]-end[0]);
        return horizontals+verticals;
    }


    private int find_min_f_cost_index(Vector<Node> list){
        Node node = list.get(0);
        int counter = 1;
        int index = 0;

        while(counter < list.size()){
            Node actual = list.get(counter);
            index = (actual.f_cost < node.f_cost)? counter : index;
            counter++;
        }

        return index;
    }


    private class Node{

        public Node front_child;
        public Node back_child;
        public Node left_child;
        public Node right_child;
        public int[] coordinates; // coordinate [x, y]
        public int g_cost;
        public int h_cost;
        public int f_cost;

        public Node(){
            this.initialize();
            this.coordinates = new int[2];
        }

        public Node(int[] _coordinates){
            this.initialize();
            this.coordinates = _coordinates;
        }

        private void initialize(){
            this.front_child = null;
            this.back_child = null;
            this.left_child = null;
            this.right_child = null;
            this.g_cost = 0;
            this.h_cost = 0;
            this.f_cost = 0;
        }

        public boolean equals(Node node){
            return this.coordinates[0] == node.coordinates[0] && this.coordinates[1] == node.coordinates[1];
        }

        public boolean equals(int[] cooridnates){
            return this.coordinates[0] == cooridnates[0] && this.coordinates[1] == cooridnates[1];
        }
    }
}
