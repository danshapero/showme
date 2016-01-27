(ns showme.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [showme.mesh :as mesh])
  (:import java.awt.event.KeyEvent))

(defn- physical-to-pixel [[width height] [xmin ymin] [x0 y0] diam]
  (fn [[x y]]
    [(* (- (/ (- x xmin) diam) x0) width)
     (* (- (- 1.0 (/ (- y ymin) diam)) y0) height)]))

(defn setup [mesh]
  (fn []
    {:mesh mesh, :x0 0.0, :y0 0.0}))

(defn draw [{:keys [mesh x0 y0]}]
  (q/background 255)
  (let [{:keys [x y edges]} mesh]
    (let [xmin (apply min x)
          ymin (apply min y)
          diam (max (- (apply max x) xmin)
                    (- (apply max y) ymin))]
      (let [f (physical-to-pixel [(q/width) (q/height)]
                                 [xmin ymin]
                                 [x0 y0]
                                 diam)]
        (doseq [k (range 0 (count edges))]
          (let [[i j] (get edges k)]
            (q/line (f [(get x i) (get y i)])
                    (f [(get x j) (get y j)]))))))))

(defn key-press [state event]
  (let [key (:key event)]
    (cond
      (= :esc key) (q/exit)
      (= :up key) (assoc state :y0 (- (:y0 state) 0.1))
      (= :down key) (assoc state :y0 (+ (:y0 state) 0.1))
      (= :right key) (assoc state :x0 (+ (:x0 state) 0.1))
      (= :left key) (assoc state :x0 (- (:x0 state) 0.1))
      :else state)))

(defn -main [& args]
  (let [filename (first args)]
    (let [mesh (mesh/read-mesh filename)]
      (q/defsketch draw-mesh
        :size [300 300]
        :middleware [m/fun-mode]
        :setup (setup mesh)
        :draw draw
        :key-pressed key-press))))
