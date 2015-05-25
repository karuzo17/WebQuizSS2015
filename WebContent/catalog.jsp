<div id="catalog" >
			<h2><img src="catalog.svg" alt="Hier fehlt was!" width="25" height="25" class="title">Catalogs</h2>
			<hr class="line"/>
			<% 
			if(quiz!=null){
				Map<String, Catalog> catalogList = quiz.getCatalogList();
				if(!catalogList.isEmpty()){
	        		for (Map.Entry<String, Catalog> e : catalogList.entrySet()) {%>
	        		<div class="catalogDiv"><%= e.getValue().getName().toString()%></div>
	        			<% 
	        		}
			}
			}

			%>
</div>