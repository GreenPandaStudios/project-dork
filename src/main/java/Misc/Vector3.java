package Misc;

public class Vector3 {

    Vector3(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public int x = 0;
    public int y = 0;
    public int z = 0;

    //A few predefined nornmalized vectors
    public static Vector3 Up = new Vector3(0,1,0);
    public static Vector3 Down = negate(Up);
    public static Vector3 Right = new Vector3(1, 0, 0);
    public static Vector3 Left = negate(Right);
    public static Vector3 Forward = new Vector3(0,0,1);
    public static Vector3 Backward = negate(Forward);


    /**
     * Subtracts v1 from v2 and returns the resulting vector
     * @param v1
     * @param v2
     * @return
     */
    public static Vector3 subtract(Vector3 v1, Vector3 v2){
        return  add(v1, negate(v2));
    }

    /**
     * Adds the two provided vectors, and returns the result
     * @param v1
     * @param v2
     * @return
     */
    public  static Vector3 add(Vector3 v1, Vector3 v2){
        return  new Vector3(v1.x + v2.x,
                v1.y + v2.y,
                v1.z + v2.z);
    }

    /**
     * Creates a new vector that is the inverse of v
     * @param v
     * @return
     */
    public  static  Vector3 negate(Vector3 v){
        return new Vector3(-v.x, -v.y, -v.z);
    }

    /**
     * returns the magnitude of the provided vector
     * @param v
     * @return
     */
    public static double magnitude (Vector3 v){
        return  Math.sqrt(((double) v.x) * ((double) v.x) +
                ((double) v.y) * ((double) v.y) +
                ((double) v.z) * ((double) v.z));
    }


    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", x,y,z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Vector3){
            Vector3 v = (Vector3) obj;
            return  (
                    v.x == this.x &&
                    v.y == this.y &&
                    v.z == this.z
                    );
        }
        return  false;
    }
}
