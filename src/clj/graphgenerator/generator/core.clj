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
  (fn [graph] (:type graph)))


;;
;; "Generate graph using Graphviz Dot. The parameter src is a source of
;; the Dot data (stream, string, ...).
;;
;; Throw an ex-info with :msg if something goes wrong."
(def valid-commands #{"dot" "neato" "twopi" "circo" "fdp"})
(def output-encodings {"svg" "UTF-8"
                       "png" :bytes})

(defmethod generate-graph :dot [{:keys [src program output]}]
  ;; todo malli for validation
  (when-not (contains? valid-commands program)
    (throw (ex-info "Invalid command" {:data program})))
  (when (and (some? output)
             (not (contains? output-encodings output)))
    (throw (ex-info "Invalid output type" {:data output})))

  (let [command       (str "/usr/bin/" program)
        output-type   (or output "svg")
        output-param  (str "-T" output-type)
        encoding      (get output-encodings output-type "UTF-8")
        {:keys [exit
                out
                err]} (shell/sh command
                                output-param
                                :in src
                                :out-enc encoding)]
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
