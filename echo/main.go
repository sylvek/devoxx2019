package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/helloworld", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprint(w, "Devoxx")
	})

	log.Fatal(http.ListenAndServe(":8888", nil))

}
