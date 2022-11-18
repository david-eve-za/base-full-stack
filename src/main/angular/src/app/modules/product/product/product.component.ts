import {Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {ProductService} from "../../shared/services/product.service";

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'price', 'stock', 'category','image', 'actions'];
  dataSource = new MatTableDataSource<ProductElement>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private productService: ProductService) {
  }

  ngOnInit(): void {
    this.getProducts();
    this.dataSource.paginator = this.paginator;
  }

  getProducts(){
    this.productService.getProducts().subscribe(products => {
      this.dataSource.data = products as ProductElement[];
      // this.dataSource.data = products;
    }, error => {
      console.log(error);
    });
  }

}

export interface ProductElement {
  id: number;
  name: string;
  price: number;
  stock: number;
  category: any;
  image: any;
}
