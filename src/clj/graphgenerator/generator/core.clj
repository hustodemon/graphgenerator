(ns graphgenerator.generator.core
  "
  Generate graphs using various tools
  "
  (:require
   [graphgenerator.generator.config :as config]
   [rhizome.viz :as viz]
   [clojure.edn :as edn]
   [clojure.java.shell :as shell]))


(defmulti generate-graph
  "Multimethod for generating graphs"
  :type)


;;
;; "Generate graph using Graphviz Dot. The parameter src is a source of
;; the Dot data (stream, string, ...).
;;
;; Throw an ex-info with :msg if something goes wrong."
(def output-encodings {"svg" "UTF-8"
                       "png" :bytes})


(defmethod generate-graph :graphviz [{:keys [src program fmt]}]
  ;; todo use malli for validation
  ;; also validate all required params are present
  (when-not (contains? config/graphviz-programs program)
    (throw (ex-info "Invalid command" {:data program})))

  (let [command       (get-in config/graphviz-programs [program :executable])
        format        (config/find-format-by-parameter :id (:id fmt)) ;; don't trust anybody
        format-param  (str "-T" (:output-format format))
        encoding      (:file-encoding format)
        {:keys [exit
                out
                err]} (shell/sh command format-param :in src :out-enc encoding)]
    (if (= 0 exit)
      out
      (throw (ex-info "Graphviz error" {:msg err})))))


;; "Generate graph using Rhizome tool."
(defmethod generate-graph :rhizome [{:keys [src]}]
  ;; src is a stream containing edn as string
  (when (instance? java.lang.String src)
    (throw (ex-info "We do not welcome strings here" {})))
  (let [parsed (-> src slurp edn/read-string)]
    (viz/graph->svg
     (keys parsed)
     parsed
     :node->descriptor (fn [n] {:label n}))))
