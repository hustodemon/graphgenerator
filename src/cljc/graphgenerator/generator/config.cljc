(ns graphgenerator.generator.config
  "
  Generators configuration
  ")
;; todo extract to file (+expose to frontend via HTTP) so that recompilation is not needed


(def max-source-length-bytes 8192)


(def formats
  [{:id :svg :file-encoding "UTF-8" :output-format "svg" :media-type "image/svg+xml"}
   {:id :png :file-encoding :bytes  :output-format "png" :media-type "image/png"}
   {:id :pdf :file-encoding :bytes  :output-format "pdf" :media-type "application/pdf"}])


(def graphviz-programs
  {:dot   {:executable "/usr/bin/dot"
           :label      "Dot"}
   :neato {:executable "/usr/bin/neato"
           :label      "Neato"}
   :twopi {:executable "/usr/bin/twopi"
           :label      "Twopi"}
   :circo {:executable "/usr/bin/circo"
           :label      "Circo"}
   :fdp   {:executable "/usr/bin/fdp"
           :label      "FDP"}})


(defn find-format-by-parameter [param value]
  (first (filter #(= value (get % param)) formats)))
