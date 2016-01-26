(ns showme.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [showme.mesh :as mesh])
  (:import java.awt.event.KeyEvent))

(def filename (atom nil))

(defn- physical-to-pixel [[width height] [xmin ymin] diam]
  (fn [[x y]]
    [(* (/ (- x xmin) diam) width)
     (* (- 1.0 (/ (- y ymin) diam)) height)]))

(defn draw [_]
  (q/background 255)
  (let [{:keys [x y edges]} (mesh/read-mesh @filename)]
    (let [xmin (apply min x)
          ymin (apply min y)
          diam (max (- (apply max x) xmin)
                    (- (apply max y) ymin))]
      (let [f (physical-to-pixel [(q/width) (q/height)] [xmin ymin] diam)]
        (doseq [k (range 0 (count edges))]
          (let [[i j] (get edges k)]
            (q/line (f [(get x i) (get y i)])
                    (f [(get x j) (get y j)]))))))))

(defn key-press [_ event]
  (when (= KeyEvent/VK_ESCAPE (:raw-key event))
    (q/exit)))

(q/defsketch draw-mesh
  :size [300 300]
  :middleware [m/fun-mode]
  :draw draw
  :key-pressed key-press)

(defn -main [& args]
  (reset! filename (first args)))
