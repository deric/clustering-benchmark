#!/bin/ruby

datasets=[]
Dir["src/main/resources/datasets/artificial/*.arff"].each do |f|
  datasets << File.basename(f)
end

def basename(file)
  file.sub(/\..*/, '')
end


datasets.sort.each do |data|
  base = basename(data)
img = <<-EOS
<a href="https://github.com/deric/clustering-benchmark/blob/master/src/main/resources/datasets/artificial/#{data}"><img src="https://github.com/deric/clustering-benchmark/blob/images/fig/artificial/#{base}.png" alt="#{base}" title="#{base}" width="250px" style="max-width: 100%;float:left;"/></a>
EOS
puts img
end

