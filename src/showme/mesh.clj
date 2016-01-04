(ns showme.mesh
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- parse-num [s]
  (when (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn- parse-file [filename]
  (let [contents (str/split (slurp filename) #"\n")]
    (mapv #(mapv parse-num (str/split (str/triml %) #"\s+")) contents)))

(defn read-poly [filename]
  (let [contents (parse-file filename)]
    (let [num-points (first (first contents))
          X (mapv #(let [[_ x y _] %] [x y])
                  (take num-points (rest contents)))]
      (let [contents (drop (inc num-points) contents)
            num-edges (first (first contents))
            edges (mapv #(let [[_ i j _] %] [(dec i) (dec j)])
                        (take num-edges (rest contents)))]
        {:x (mapv first X)
         :y (mapv second X)
         :edges edges}))))

(defn- read-node [filename]
  (let [contents (parse-file filename)
        num-points (first (first contents))]
    (mapv #(let [[_ x y _] % ] [x y])
          (take num-points (rest contents)))))

(defn- read-ele [filename]
  (let [contents (parse-file filename)
        num-triangles (first (first contents))]
    (mapv #(let [[_ i j k ] %] [(dec i) (dec j) (dec k)])
          (take num-triangles (rest contents)))))

(defn- read-edge [filename]
  (let [contents (parse-file filename)
        num-edges (first (first contents))]
    (mapv #(let [[_ i j _] %] [(dec i) (dec j)])
          (take num-edges (rest contents)))))

(defn- triangles-to-edges [triangles]
  (into []
        (reduce (fn [edges [i j k]]
                     (conj edges [i j] [j k] [k i]))
                   #{} triangles)))

(defn read-mesh [filename]
  (if (= ".poly" (subs filename (- (count filename) (count ".poly"))))
    (read-poly filename)
    (let [X (read-node (str/join [filename ".node"]))
          edges (if (.exists (io/as-file (str/join [filename ".edge"])))
                  (read-edge (str/join [filename ".edge"]))
                  (triangles-to-edges (read-ele (str/join [filename ".ele"]))))]
      {:x (mapv first X)
       :y (mapv second X)
       :edges edges})))
