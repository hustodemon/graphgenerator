(ns graphgenerator.generator.config
  "
  Generators configuration
  ")
;; todo extract to file (+expose to frontend via HTTP) so that recompilation is not needed


(def formats
  [{:id :svg :file-encoding "UTF-8" :graphviz-param "svg" :media-type "image/svg+xml"}
   {:id :png :file-encoding :bytes  :graphviz-param "png" :media-type "image/png"}
   {:id :pdf :file-encoding :bytes  :graphviz-param "pdf" :media-type "application/pdf"}])


(def graphviz-programs
  {:dot   {:executable "/usr/bin/doc"
           :label      "Dot"}
   :neato {:executable "/usr/bin/doc"
           :label      "Neato"}})


(defn find-format-by-media-type [media-type]
  (first (filter #(= media-type (:media-type %)) formats)))
