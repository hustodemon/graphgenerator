(ns graphgenerator.generator.config
  "
  Generators configuration
  ")
;; todo extract to file (+expose to frontend via HTTP) so that recompilation is not needed


(def formats
  [{:id :svg :file-encoding "UTF-8" :output-format "svg" :media-type "image/svg+xml"}
   {:id :png :file-encoding :bytes  :output-format "png" :media-type "image/png"}
   {:id :pdf :file-encoding :bytes  :output-format "pdf" :media-type "application/pdf"}])


(def graphviz-programs
  {:dot   {:executable "/usr/bin/dot"
           :label      "Dot"}
   :neato {:executable "/usr/bin/neato"
           :label      "Neato"}})


(defn find-format-by-parameter [param value]
  (first (filter #(= value (get % param)) formats)))
