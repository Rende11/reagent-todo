(ns ^:figwheel-no-load re-example.dev
  (:require
    [re-example.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
