(ns showme.core
  (:require [quil.core :as q]
            [showme.mesh :as mesh]))

(def filename (atom nil))

(defn draw []
  (q/background 255)
  (let [{:keys [x y edges]} (mesh/read-poly @filename)]
    (let [xmin (apply min x)
          ymin (apply min y)
          diam (max (- (apply max x) xmin)
                    (- (apply max y) ymin))]
      (letfn [(f [t] (* (/ (- t xmin) diam) (q/width)))
              (g [t] (* (/ (- t ymin) diam) (q/height)))]
        (doseq [k (range 0 (count edges))]
          (let [[i j] (get edges k)]
            (q/line [(f (get x i)) (g (get y i))]
                    [(f (get x j)) (g (get y j))])))))))

(q/defsketch draw-mesh
  :size [300 300]
  :draw draw)

(defn -main [& args]
  (reset! filename (first args)))
