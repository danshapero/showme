(ns showme.mesh
  (:require [clojure.string :as str]))

(defn- parse-num [s]
  (when (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn- parse-file [filename]
  (let [contents (str/split (slurp filename) #"\n")]
    (map #(map parse-num (str/split % #" ")) contents)))

(defn read-poly [filename]
  (let [contents (parse-file filename)]
    (let [num-points (first (first contents))
          X (map #(let [[_ x y _] %] [x y])
                 (take num-points (rest contents)))]
      (let [contents (drop (inc num-points) contents)
            num-edges (first (first contents))
            edges (mapv #(let [[_ i j _] %] [(dec i) (dec j)])
                        (take num-edges (rest contents)))]
        {:x (mapv first X)
         :y (mapv second X)
         :edges edges}))))
