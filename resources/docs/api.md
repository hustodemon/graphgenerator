# API

You can use the application by calling a HTTP POST request with the following params:
- query param `type`, valid values: `graphviz | rhizome` 
- query param `tool`, valid values: `dot | neato | twopi | circo | fdp`
- header `Accept`, valid values: `image/svg+xml`, `image/png`, `application/pdf`
- request body containing graph description

All params except of the request body are optional and they default to this combination:
`graphviz, dot, image/svg+xml`.


Generate a simple graph using Dot into a PNG image
```bash
# Generate a simple graph with Dot program as PNG
curl 'http://SERVER/generate?type=graphviz&program=dot' \
  -H 'Accept: image/png' \
  -H 'Content-Type: text/plain; charset=UTF-8' \
  --data-raw $'graph { a -- b }\n' \
  --output out.png
```

Generate a simple graph using Circo into an SVG graphics
```bash
# Generate a simple graph with Dot program as PNG
curl 'http://SERVER/generate?type=graphviz&program=circo' \
  -H 'Accept: image/svg+xml' \
  -H 'Content-Type: text/plain; charset=UTF-8' \
  --data-raw $'graph { a -- b; b -- c; c -- d; d --a; }\n' \
  --output out.svg
```

