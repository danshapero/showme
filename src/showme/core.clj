(ns showme.core
  (:require [quil.core :as q]
            [showme.mesh :as mesh]))

(def filename (atom nil))

(defn- physical-to-pixel [[width height] [xmin ymin] diam]
  (fn [[x y]]
    [(* (/ (- x xmin) diam) width)
     (* (- 1.0 (/ (- y ymin) diam)) height)]))

(defn draw []
  (q/background 255)
  (let [{:keys [x y edges]} (mesh/read-poly @filename)]
    (let [xmin (apply min x)
          ymin (apply min y)
          diam (max (- (apply max x) xmin)
                    (- (apply max y) ymin))]
      (let [f (physical-to-pixel [(q/width) (q/height)] [xmin ymin] diam)]
        (doseq [k (range 0 (count edges))]
          (let [[i j] (get edges k)]
            (q/line (f [(get x i) (get y i)])
                    (f [(get x j) (get y j)]))))))))

(q/defsketch draw-mesh
  :size [300 300]
  :draw draw)

(defn -main [& args]
  (reset! filename (first args)))
