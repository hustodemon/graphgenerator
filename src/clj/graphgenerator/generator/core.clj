(ns graphgenerator.generator.core
  "
  Generate graphs using various tools
  "
  (:require
   [rhizome.viz :as viz]
   [clojure.java.shell :as shell]))


(defn generate-graph-rhizome
  "Generate graph using Rhizome tool."
  [graph-edn]
  ;; definitely space for customization of stuff like node->descriptor
  (viz/graph->svg (keys graph-edn)
                  graph-edn
                  :node->descriptor (fn [n] {:label n})))


(defn generate-graph-dot
  "Generate graph using Graphviz Dot"
  [dot-str]
  (let [{:keys [exit out err]} (shell/sh "/usr/bin/dot" "-Tsvg" :in dot-str)]
    (if (= 0 exit)
      out
      (throw (ex-info "Dot error" {:msg err})))))
